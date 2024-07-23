package it.pdf;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.faceless.pdf2.*;

/** 
 * An example demonstrating how to "preflight" a document - in this case, to convert it
 * to PDF/A. This example has been completely revised for the "new" (as of 2021)
 * preflighting technique, and the class itself - minus the static methods - is a useful
 * template for your own code.
 *
 * An example use might be
 *
 *  java ConvertToPDFA --font arial.ttf --font NotoSansCJKsc-Regular.otf --icc srgb --icc fogra39-300.icc file.pdf
 *
 *
 * Changes
 * -------
 *  2023-01-19  added getStrategy() method
 *
 *  2022-09-12  choose a CMYK profile over an RGB one if PDF has overprint, separations or CMYK color (previously
 *              it was overprint, separations or didn't contains RGB color). Added getOutputProfiler() and
 *              getStrategy() methods.
 *
 *  2022-01-31  added the modifyTarget() method, and if the profile is modified with this approach ensure its also
 *              the profile returned from getUsedTarget()
 *
 */
public class ConvertToPDFA5 {

    public static final int STATE_NEW = 0;          // PDF preflight has not been run
    public static final int STATE_VALID = 1;        // PDF was already valid, no changes were made
    public static final int STATE_FIXED = 2;        // PDF was modified to meet the profile
    public static final int STATE_FIXED_BITMAP = 3; // PDF was modified to meet the profile by rasterizing at least one page.
    public static final int STATE_FAILED = 4;       // PDF failed to convert

    private PDFParser parser;
    private OutputProfiler profiler;
    private ColorSpace intentcs;
    private Collection<ColorSpace> colorSpaces;
    private Collection<OutputProfile> allowedTargets;   // The OutputProfiles we will accept as a target
    private Collection<OutputProfile> retainedTargets;  // Additional OutputProfiles we will keep if already set.
    private OutputProfile defaultTarget;                // If no allowed target is set in the PDF, the target to aim for
    private OutputProfile usedTarget;                   // The target actually used
    private List<OpenTypeFont> fontList;                // Font fonts to substitute in if required
    private int state;                                  // a STATE_ value - the state of the ConvertToPDFA object
    private String message;                             // a message decribing the state, or null

    /**
     * Create a new ConvertToPDFA instance
     * @param pdf the PDF to be procesed
     */
    public ConvertToPDFA5(PDF pdf) {
        this.parser = new PDFParser(pdf);
        this.profiler = new OutputProfiler(parser);
        this.state = STATE_NEW;

        setStrategy(OutputProfiler.Strategy.JustFixIt);
        setDefaultTarget(OutputProfile.PDFA1a_2005);
        setTargetProfiles(null);
        setColorSpaces(null);
        setRetainedProfiles(Arrays.asList(OutputProfile.PDFX4, OutputProfile.PDFUA1, OutputProfile.ZUGFeRD1_Basic, OutputProfile.ZUGFeRD1_Comfort, OutputProfile.ZUGFeRD1_Extended));
    }

    /**
     * Return the OutputProfiler used to do the conversion
     */
    public OutputProfiler getOutputProfiler() {
        return profiler;
    }

    /**
     * Set the list of ColorSpace objects to use to "anchor" device-dependent colors
     * in the PDF. The ColorSpace objects should be ICC-based, and really should
     * include one RGB and one CMYK profile. The sRGB profile returned from
     * <code>Color.red.getColorSpace</code> is a good one to use.
     *
     * If you don't have an appropriate profile, many are available from
     * https://www.color.org/registry/index.xalter - We recommend
     *   -- FOGRA39 in Europe ("Coated FOGRA 39 300" is a good choice)
     *   -- SWOP2013 in the Americas
     *   -- Japan Color 2011 in Japan
     * all of which are available for download from the above website.
     *
     * @param list the list of ColorSpaces to use to calibrate colors in the PDF
     */
    public void setColorSpaces(List<? extends ColorSpace> list) {
        this.colorSpaces = list == null ? Collections.<ColorSpace>emptyList() : new ArrayList<ColorSpace>(list);
    }

    /**
     * Set the ColorSpace to use for the OutputIntent. It is usually best to leave
     * this set to null (the default), in which case the OuptutIntent ColorSpace
     * will be chosen from the list supplied to {@link #setColorSpaces}.
     * @param cs the ColorSpace for the OutputIntent, or null to choose automatically
     */
    public void setOutputIntentColorSpace(ColorSpace cs) {
        this.intentcs = cs;
    }

    /**
     * Specify the set OutputProfiles the PDF will be matched to. If the PDF already
     * claims to be compliant to one of these, that's what we'll be matched against.
     * If the value of "targets" is empty, we'll ignore any claims in the PDF.
     */
    public void setTargetProfiles(Collection<OutputProfile> targets) {
        this.allowedTargets = targets == null ? Collections.<OutputProfile>emptyList() : new ArrayList<OutputProfile>(targets);
    }

    /**
     * Specify a set of OutputProfiles the PDF will keep if the PDF already meets
     * them, but which we will not otherwise try to match. Adding (for example)
     * PDF/UA-1 here ensures that if the PDF complied with PDF/UA-1 when loaded, it
     * will still comply with PDF/UA-1 when saved. The default set is PDF/X-4,
     * PDF/UA-1 and ZUGFeRD, and should cause no problems.
     */
    public void setRetainedProfiles(Collection<OutputProfile> targets) {
        this.retainedTargets = targets == null ? Collections.<OutputProfile>emptyList() : new ArrayList<OutputProfile>(targets);
        for (Iterator<OutputProfile> i = retainedTargets.iterator();i.hasNext();) {
            OutputProfile p = i.next();
            // We can't support merging with profiles that only allow one OutputIntent,
            // as we're going to be adding a GTS_PDFA1 intent. This rules out PDF/X-1 and X-3
            if (p.isDenied(OutputProfile.Feature.HasMultipleOutputIntents)) {
                i.remove();
            }
        }
    }

    /**
     * Set the OutputProfile to use as a target profile if none of the profiles
     * set by {@link #setTargetProfiles} apply.
     */
    public void setDefaultTarget(OutputProfile defaultTarget) {
        if (defaultTarget == null) {
            throw new IllegalArgumentException("Default target cannot be null");
        }
        this.defaultTarget = defaultTarget;
    }

    /**
     * Set the list of Fonts to consider for substitution into the PDF in place of
     * unembedded fonts. The most common unembedded fonts are the standard Microsoft
     * System fonts - Times, Arial, Trebuchet etc. - and Chinese/Japanese/Korean
     * fonts, for which we recommend including at least one of Noto CJK fonts from
     * https://www.google.com/get/noto/help/cjk/
     */
    public void setFontList(List<OpenTypeFont> fonts) {
        this.fontList = fonts;
    }

    /**
     * Set the list of {@link OutputProfiler.Strategy} choices, to control how
     * the PDF is converted. The default is {@link OutputProfiler.Strategy#JustFixIt}
     * which should give 100% conversion success.
     */
    public void setStrategy(OutputProfiler.Strategy... strategy) {
        profiler.setStrategy(strategy);
    }

    /**
     * Return the currently used list of {@link OutputProfiler.Strategy} choices.
     * @see #setStrategy
     */
    public List<OutputProfiler.Strategy> getStrategy() {
        return profiler.getStrategy();
    }

    /**
     * Return the current state of the preflight process.
     */
    public int getState() {
        return state;
    }

    /**
     * Return any message describing the state of the preflight process
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the OutputProfile this PDF was eventually profiled against.
     */
    public OutputProfile getUsedTarget() {
        return usedTarget;
    }

    /**
     * Run the preflight, and set the state and message as a result.
     */
    public void run() {
        if (state != STATE_NEW) {
            throw new IllegalStateException("Already run");
        }
        retainedTargets.removeAll(allowedTargets);

        // Step 1. Profile the PDF. If we've set a list of allowable "target" profiles, see if it
        // matches any of those. If it does, we don't need to do anything: the PDF is already valid.
        OutputProfile profile = profiler.getProfile();
        Collection<OutputProfile> claimedTargets = profile.getClaimedTargetProfiles();

        // Choose a target from our allowed list
        usedTarget = defaultTarget;
        for (OutputProfile p : claimedTargets) {
            if (allowedTargets.contains(p)) {
                usedTarget = p;
                break;
            }
        }
        // Check if it's already valid.
        if (profile.isCompatibleWith(usedTarget) == null) {
            state = STATE_VALID;
            return;
        }

        // Retain any additional targets we already meet, so long as they're
        // compatible with our chosen target and in our allowed list.
        OutputProfile target = new OutputProfile(usedTarget);
        for (OutputProfile p : claimedTargets) {
            boolean retain = allowedTargets.contains(p);
            if (retainedTargets.contains(p) && profile.isCompatibleWith(p) == null) {
                try {
                    target.merge(p, profile);
                    retain = true;
                } catch (ProfileComplianceException e) {
                    // This combination is disallowed.
                }
            }
            if (!retain) {
                // Remove any claims we can't meet from the PDF. Not strictly
                // required, but we shouldn't be writing a PDF that claims compliance
                // to (for example) PDF/UA-1 if we know the PDF doesn't comply.
                // System.out.println("Removing claim \"" + p.getProfileName() + "\"");
                target.denyClaim(p);
            }
        }

        // Now we try and determine the default ColorSpace for the PDF - the "output intent" -
        // which will not only determine the colorimetry for any device-dependent colors in the PDF,
        // but will also affect how the PDF is displayed in Acrobat. Broadly the approach is:
        // 1. If the PDF specifies one try to reuse it.
        // 2. Otherwise try to guess whether the PDF is mostly print, or mostly screen, and choose
        //    a CMYK or RGB profile accordingly.

        if (intentcs != null) {
            // OutputIntent Test 1: if a valid ColorSpace for the OutputIntent has been specified explicity, use it.
            OutputIntent intent = new OutputIntent("GTS_PDFA1", null, intentcs);
            if (intent.isCompatibleWith(target) == null) {
                target.getOutputIntents().add(intent);
            }
        }
        if (target.getOutputIntent("GTS_PDFA1") == null) {
            // OutputIntent Test 2: otherwise, if a valid PDF/A OutputIntent exists in the PDF, use it.
            OutputIntent intent = profile.getOutputIntent("GTS_PDFA1");
            if (intent != null && intent.isCompatibleWith(target) == null) {
                target.getOutputIntents().add(new OutputIntent(intent));
            }
        }
        for (OutputIntent intent : profile.getOutputIntents()) {
            // OutputIntent Test 3: otherwise, if any other valid OutputIntent exists in the PDF, use it
            // for PDF/A. If the PDF is PDF/X and we want to keep it that way, use it for that too.
            if (intent.isCompatibleWith(target) == null) {
                if (target.getRequiredOutputIntentTypes().contains(intent.getType())) {
                    target.getOutputIntents().add(new OutputIntent(intent));
                }
                if (target.getOutputIntent("GTS_PDFA1") == null) {
                    target.getOutputIntents().add(new OutputIntent("GTS_PDFA1", intent));
                }
            }
        }

        // Pass all the ColorSpaces we've been given to use into a new ProcessColorAction.
        List<ColorSpace> cslist = new ArrayList<ColorSpace>();
        cslist.addAll(profile.getICCColorSpaces());     // Add any ICC ColorSpaces used anywhere in the PDF
        cslist.addAll(colorSpaces);
        OutputProfiler.ProcessColorAction colorAction = new OutputProfiler.ProcessColorAction(target, cslist);
        if (target.getOutputIntent("GTS_PDFA1") == null) {
            // OutputIntent Test 4: we still don't have one. Choose either a CMYK or RGB
            // ColorSpace from the list we've been given - but which one?
            // If the PDF looks like it's print focused, using CMYK will give better results.
            // So if the PDF has a Color Separation called "Cyan", "Magenta" or "Yellow", or
            // it uses a CMYK Blend Mode, or it uses Overprinting, prefer a CMYK profile.
            boolean cmyk = profile.isSet(OutputProfile.Feature.ColorSpaceDeviceCMYK) ||
                           profile.isSet(OutputProfile.Feature.AnnotationColorSpaceDeviceCMYK) ||
                           profile.isSet(OutputProfile.Feature.NChannelProcessDeviceCMYK) ||
                           profile.isSet(OutputProfile.Feature.ColorSpaceDeviceCMYKInMatchingParent) ||
                           profile.isSet(OutputProfile.Feature.TransparencyGroupCMYK) ||
                           profile.isSet(OutputProfile.Feature.Overprint);
            for (OutputProfile.Separation s : profile.getColorSeparations()) {
                String name = s.getName();
                cmyk |= name.equals("Cyan") || name.equals("Magenta") || name.equals("Yellow");
            }
            ColorSpace cs = cmyk ? colorAction.getDeviceCMYK() : colorAction.getDeviceRGB();
            if (cs == null) { // First choice not available, fall back to the other.
                cs = !cmyk ? colorAction.getDeviceCMYK() : colorAction.getDeviceRGB();
            }
            if (cs != null) {
                OutputIntent intent = new OutputIntent("GTS_PDFA1", null, cs);
                if (intent.isCompatibleWith(target) == null) {
                    target.getOutputIntents().add(intent);
                    // We've changed the OutputIntent on the target, so recreate the ColorAction
                    colorAction = new OutputProfiler.ProcessColorAction(target, cslist);
                }
            } else {
                // No OutputIntent set. This isn't necessarily fatal; if the PDF already contains
                // only calibrated colors (and for PDF/A-2 or later, no overprinting) then it may
                // be OK. But generally, if you get here you need more ColorSpaces in cslist.
            }
        }
        // Warn if colorspaces are missing, because things are probably going to go wrong.
        if (colorAction.getDeviceCMYK() == null) {
            if (colorAction.getDeviceRGB() == null) {
                System.err.println("WARNING: ConvertToPDFA: ColorAction has no sRGB or CMYK profile, conversion may fail");
            } else {
                System.err.println("WARNING: ConvertToPDFA: ColorAction has no CMYK profile, conversion may fail");
            }
        } else if (colorAction.getDeviceRGB() == null) {
            System.err.println("WARNING: ConvertToPDFA: ColorAction has no RGB profile, conversion may fail");
        }
        profiler.setColorAction(colorAction);

        // Set the fonts to use. Note we clone the fonts passed in here - PDFs
        // should not share fonts! Clone with "new OpenTypeFont(font)"
        OutputProfiler.AutoEmbeddingFontAction fontAction = new OutputProfiler.AutoEmbeddingFontAction();
        for (OpenTypeFont f : fontList) {
            fontAction.add(new OpenTypeFont(f));
        }
        // This next line is what we're assuming you want - "just fix it". Other strategies
        // are available, but will inevitaby result in more conversion failures. See the API
        // docs for details.
        profiler.setFontAction(fontAction);
//        profiler.setRasterizingActionExecutorService(Executors.newFixedThreadPool(3));
        profiler.setRasterizingAction(new OutputProfiler.RasterizingAction() {
            // We override this only so we can get some logging out of it. Normally not necessary
            @Override public void rasterize(OutputProfiler profiler, PDFPage page, OutputProfile pageProfile, ProfileComplianceException e) {
                state = STATE_FIXED_BITMAP;
                message = e == null ? "Rasterized" : "Rasterized due to " + e.getFeature().getFieldName();
                super.rasterize(profiler, page, pageProfile, e);
            }
        });
        // Setup all done. Apply the target profile.
        try {
            state = STATE_FIXED;
            target = modifyTarget(target);
            profiler.apply(target);
        } catch (RuntimeException e) {
            state = STATE_FAILED;
            message = "Failed: " + e.getMessage();
            throw e;
        }
        // Finally, recheck which target was actually used. The "AutoConformance"
        // strategy can change a requested target from PDF/A-2a to PDF/A-2b, for example.
        // So ask the Profiler which one it used.
        // getProfile() will return instantly here, it's already calculated.
        profile = profiler.getProfile();
        boolean found = false;
        for (OutputProfile p : profile.getClaimedTargetProfiles()) {
            if (p == defaultTarget || allowedTargets.contains(p)) {
                usedTarget = p;
                found = true;
                break;
            }
        }
        if (!found) {
            // If we didn't find which target we used in the list of "claimed targets",
            // it's because some sort of customized target was passed in. The best
            // we can do here is to return the actual target that was used.
            usedTarget = target;
        }
    }

    /**
     * This method is the best place to modify the OutputProfile being targeted.
     * For example, if you wanted to remove compression or pretty-print the XMP
     * when its saved, here where to do it.
     * This method should return the modified profile - typically this would
     * be the profile thats's passed in. The default implemention simply
     * returns "target" with no changes.
     * @param target the OutputProfile to modify and return
     * @return the target profile to use
     */
    protected OutputProfile modifyTarget(OutputProfile target) {
        // eg target.setRequired(OutputProfile.Feature.XMPMetaDataPretty);
        // eg target.setDenied(OutputProfile.Feature.RegularCompression);
        return target;
    }

    /**
     * A helper method to quickly verify a PDF against a specified OutputProfile.
     * Returns null if the PDF is valid and matches, otherwise returnn a list of
     * reasons why the PDF failed to verify ("+" means the feature was required
     * but missing, "-" means the feature was set but disallowed)
     * @param pdf the PDF, which should be newly loaded
     * @param target the OutputProfile to verify against.
     */
    public static String verify(PDF pdf, OutputProfile target) {
        OutputProfiler profiler = new OutputProfiler(new PDFParser(pdf));
        OutputProfile profile = profiler.getProfile();
        OutputProfile.Feature[] mismatch = profile.isCompatibleWith(target);
        if (mismatch == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[ERROR] Verify failed against ");
            sb.append(target.getProfileName());
            sb.append(":");
            for (OutputProfile.Feature f : mismatch) {
                sb.append(target.isRequired(f) ? " -" : " +");
                sb.append(f.toString());
            }
            return sb.toString();
        }
    }

    // ---------------------------------------------------------------------------

    private static void help() {
        System.out.println();
        System.out.println("java ConvertToPDFA [--font <file>]+ [--icc <file>]+ --<profilename> [<file.pdf>+ | --files-from <file>]");
        System.out.println("   --font <fontfile>     one or more OpenType fonts to consider for substitution into the PDF");
        System.out.println("   --icc srgb|<iccfile>  one or more ICC profiles to calibrate PDF color against");
        System.out.println("   --<profilename>       which PDF/A profiles to target. Valid options include pdfa1, pdfa2, pdfa3");
        System.out.println("                         and pdfa4");
        System.out.println("   --files-from <file>   specifies a file containing the list of PDF files to process, rether than");
        System.out.println("                         listing them all on the command line.");
        System.out.println();
        System.out.println("   For correct operation, at least two ICC profiles should be supplied; one RGB and one CMYK. The");
        System.out.println("   CMYK must be loaded from an ICC Profile file, but the value \"srgb\" can be ued to load the sRGB");
        System.out.println("   profile. Multiple fonts should be provided; we recommend at least a NotoSansCJK font, and ideally");
        System.out.println("   the Times, Arial and Courier fonts supplied with Windows. Don't forget the bold and italic variants.");
        System.out.println();
        System.out.println("   PDFs will be verified or converted against the specified PDF/A profiles, with the first one the default");
        System.out.println("   choice. If no profiles are specified, any PDF/A profile is accepted, with PDF/A-1 the default");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        List<OpenTypeFont> fonts = new ArrayList<OpenTypeFont>();
        List<OutputProfile> targets = new ArrayList<OutputProfile>();
        List<ColorSpace> colorspaces = new ArrayList<ColorSpace>();
        List<String> filenames = new ArrayList<String>();
        ColorSpace intentcs = null;

        if (args.length == 0) {
            help();
            System.exit(1);
        }
        for (int i=0;i<args.length;i++) {
            String s = args[i];
            if (s.equals("--help")) {
                help();
                System.exit(1);
            } else if (s.equals("--font")) {
                OpenTypeFont font = new OpenTypeFont(new File(args[++i]), null);
                fonts.add(font);
            } else if (s.equals("--icc")) {
                String name = args[++i];
                if (name.equalsIgnoreCase("srgb")) {
                    colorspaces.add(Color.red.getColorSpace());
                } else {
                    colorspaces.add(new ICCColorSpace(new File(name)));
                }
            } else if (s.equals("--icc-intent")) {
                String name = args[++i];
                if (name.equalsIgnoreCase("srgb")) {
                    colorspaces.add(Color.red.getColorSpace());
                } else {
                    colorspaces.add(new ICCColorSpace(new File(name)));
                }
                intentcs = colorspaces.get(colorspaces.size() - 1);
            } else if (s.equals("--pdfa1")) {
                targets.add(OutputProfile.PDFA1a_2005);
                targets.add(OutputProfile.PDFA1b_2005);
            } else if (s.equals("--pdfa2")) {
                targets.add(OutputProfile.PDFA2a);
                targets.add(OutputProfile.PDFA2b);
                targets.add(OutputProfile.PDFA2u);
            } else if (s.equals("--pdfa3")) {
                targets.add(OutputProfile.PDFA3a);
                targets.add(OutputProfile.PDFA3b);
                targets.add(OutputProfile.PDFA3u);
            } else if (s.equals("--pdfa4")) {
                targets.add(OutputProfile.PDFA4);
                targets.add(OutputProfile.PDFA4e);
                targets.add(OutputProfile.PDFA4f);
            } else if (s.equals("--files-from")) {
                BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(args[++i]), "UTF-8"));
                while ((s=r.readLine()) != null) {
                    filenames.add(s);
                }
                r.close();
            } else {
                filenames.add(s);
            }
            for (String filename : filenames) {
                File infile = new File(filename);
                PDF pdf = null;
                try {
                    pdf = new PDF(new PDFReader(infile));
                } catch (Exception e) {
                    // PDF completely failed to load - perhaps badly damaged,
                    // password protected or not a PDF? Report it and continue.
                    System.out.println(infile + " [ERROR]: " + e);
                }
                if (pdf != null) {
                    try {
                        // Convert the PDF to PDF/A
                        ConvertToPDFA5 preflight = new ConvertToPDFA5(pdf);
                        if (targets.isEmpty()) {
                            // If no target profiles specified, target any published PDF/A profile
                            // and PDF/A-1b by default.
                            preflight.setDefaultTarget(OutputProfile.PDFA1b_2005);
                            preflight.setTargetProfiles(Arrays.asList(
                                OutputProfile.PDFA1b_2005, OutputProfile.PDFA1a_2005,
                                OutputProfile.PDFA2a, OutputProfile.PDFA2b, OutputProfile.PDFA2u,
                                OutputProfile.PDFA3a, OutputProfile.PDFA3u, OutputProfile.PDFA3b,
                                OutputProfile.PDFA4, OutputProfile.PDFA4e, OutputProfile.PDFA4f));
                        } else {
                            preflight.setDefaultTarget(targets.get(0));
                            preflight.setTargetProfiles(targets);
                        }
                        preflight.setFontList(fonts);
                        if (colorspaces.isEmpty()) {
                            // This technically isn't fatal but it means you haven't specified
                            // the "--icc" parameter - twice - to specify an RGB and CMYK profile.
                            // This is probably oversight, so throw an exception.
                            throw new IllegalStateException("No ColorSpaces specified! Conversion almost always requires you to specify an RGB and CMYK colorspace");
                        }
                        preflight.setColorSpaces(colorspaces);
                        preflight.setOutputIntentColorSpace(intentcs);
                        preflight.run();

                        // Report on the results
                        OutputProfile usedTarget = preflight.getUsedTarget();
                        if (preflight.getState() == STATE_VALID) {
                            // No changes were made.
                            System.out.println(infile + " [OK]: Already valid against " + usedTarget.getProfileName());
                        } else {
                            // Changes were made. Save PDF, then load and verify.
                            // Before we save, add an entry to the "History" in the metadata
                            // noting the conversion. Optional, but metadata is always useful.
                            pdf.getXMP().addHistory("Converted to PDF/A", null, "BFOPDF " + PDF.VERSION, null, null);
                            File outfile = new File("preflight-" + infile.getName());
                            OutputStream out = new FileOutputStream(outfile);
                            pdf.render(out);
                            out.close();

                            String message = null;
                            if (true) {
                                // As an insurance check, this block reloads the PDF we
                                // just created and verifies it really is valid.
                                pdf = new PDF(new PDFReader(outfile));
                                message = verify(pdf, usedTarget);
                            }
                            if (message != null) {
                                // We thought the PDF was repaired, but it wasn't. If
                                // we've done our job properly, this block should't run.
                                System.out.println(infile + ": " + message);
                            } else {
                                // PDF successfully verified.
                                message = preflight.getMessage();
                                if (message == null) {
                                    System.out.println(infile + ": [OK] Converted to " + usedTarget.getProfileName() + " and saved as \"" + outfile + "\"");
                                } else {
                                    System.out.println(infile + ": [OK] Converted to " + usedTarget.getProfileName() + " and saved as \"" + outfile + "\" (" + message + ")");
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Something has gone wrong! You shouldn't get here, but if you
                        // do, log it and go onto the next file.
                        System.out.println(infile + ": [ERROR] " + e);
                        e.printStackTrace(System.out);
                    }
                }
            }
            filenames.clear();
        }
    }

}
    /**
     * Imposta l'elenco degli oggetti ColorSpace da utilizzare per "ancorare" i colori dipendenti dal dispositivo
     * nel PDF. Gli oggetti ColorSpace dovrebbero essere basati su ICC e dovrebbero davvero
     * include un profilo RGB e uno CMYK. Il profilo sRGB restituito da
     * <code>Color.red.getColorSpace</code> è una buona opzione da utilizzare.
     *
     * Se non hai un profilo appropriato, molti sono disponibili da
     * https://www.color.org/registry/index.xalter - Consigliamo
     * -- FOGRA39 in Europa ("Coated FOGRA 39 300" è una buona scelta)
     * -- SWOP2013 nelle Americhe
     * -- Japan Color 2011 in Giappone
     * tutti disponibili per il download dal sito web sopra indicato.
     *
     * @param elenca l'elenco degli spazi colore da utilizzare per calibrare i colori nel PDF
     */
    pubblico void setColorSpaces(List<? extends ColorSpace> elenco) {
        this.colorSpaces = list == null ? Collezioni.<ColorSpace>emptyList() : new ArrayList<ColorSpace>(list);
    }

    /**
     * Imposta il ColorSpace da usare per OutputIntent. Di solito è meglio lasciare
     * questo è impostato su null (il valore predefinito), nel qual caso OuptutIntent ColorSpace
     * verrà scelto dall'elenco fornito a {@link #setColorSpaces}.
     * @param cs il ColorSpace per OutputIntent, o null per scegliere automaticamente
     */
    pubblico void setOutputIntentColorSpace(ColorSpace cs) {
        questo.intentocs = cs;
    }

    /**
     * Specificare il set OutputProfiles a cui il PDF verrà abbinato. Se il PDF è già
     * dichiara di essere conforme a uno di questi, ed è con quello che saremo confrontati.
     * Se il valore di "targets" è vuoto, ignoreremo tutte le affermazioni nel PDF.
     */
    public void setTargetProfiles(Collection<OutputProfile> destinazioni) {
        this.allowedTargets = obiettivi == null ? Collezioni.<OutputProfile>emptyList() : new ArrayList<OutputProfile>(obiettivi);
    }

    /**
     * Specificare un set di OutputProfiles che il PDF manterrà se il PDF soddisfa già
     * loro, ma che altrimenti non cercheremo di eguagliare. Aggiungendo (ad esempio)
     * PDF/UA-1 qui assicura che se il PDF è conforme a PDF/UA-1 quando caricato,
     * sarà comunque conforme a PDF/UA-1 quando salvato. Il set predefinito è PDF/X-4,
     * PDF/UA-1 e ZUGFeRD e non dovrebbero causare problemi.
     */
    pubblico void setRetainedProfiles(Collection<OutputProfile> destinazioni) {
        this.retainedTargets = obiettivi == null ? Collezioni.<OutputProfile>emptyList() : nuovo ArrayList<OutputProfile>(obiettivi);
        per (Iterator<OutputProfile> i = retainedTargets.iterator();i.hasNext();) {
            Profilo di output p = i.next();
            // Non possiamo supportare l'unione con profili che consentono solo un OutputIntent,
            // poiché aggiungeremo un intent GTS_PDFA1. Questo esclude PDF/X-1 e X-3
            se (p.isDenied(OutputProfile.Feature.HasMultipleOutputIntents)) {
                rimuovo();
            }
        }
    }

    /**
     * Imposta OutputProfile da utilizzare come profilo di destinazione se nessuno dei profili
     * si applica l'impostazione di {@link #setTargetProfiles}.
     */
    pubblico void setDefaultTarget(OutputProfile defaultTarget) {
        se (defaultTarget == null) {
            throw new IllegalArgumentException("Il target predefinito non può essere null");
        }
        questo.defaultTarget = defaultTarget;
    }

    /**
     * Imposta l'elenco dei font da considerare per la sostituzione nel PDF al posto di
     * font non incorporati. I font non incorporati più comuni sono i font standard Microsoft
     * Caratteri di sistema - Times, Arial, Trebuchet ecc. - e cinese/giapponese/coreano
     * font, per i quali consigliamo di includere almeno uno dei font Noto CJK da
     * https://www.google.com/get/noto/help/cjk/
     */
    pubblico void setFontList(Elenco<OpenTypeFont> caratteri) {
        this.fontList = caratteri;
    }

    /**
     * Imposta l'elenco delle scelte {@link OutputProfiler.Strategy}, per controllare come
     * il PDF viene convertito. Il valore predefinito è {@link OutputProfiler.Strategy#JustFixIt}
     * che dovrebbe garantire il 100% di successo nella conversione.
     */
    pubblico void setStrategy(OutputProfiler.Strategy... strategia) {
        profiler.setStrategy(strategia);
    }

    /**
     * Restituisce l'elenco di scelte {@link OutputProfiler.Strategy} attualmente utilizzato.
     * @vedi #setStrategy
     */
    Elenco pubblico<OutputProfiler.Strategy> getStrategy() {
        restituisci profiler.getStrategy();
    }

    /**
     * Restituisce lo stato corrente del processo di preflight.
     */
    pubblico int getState() {
        stato di ritorno;
    }

    /**
     * Restituisce qualsiasi messaggio che descriva lo stato del processo di preflight
     */
    Stringa pubblica getMessage() {
        messaggio di ritorno;
    }

    /**
     * Ottieni l'OutputProfile in base al quale è stato infine profilato questo PDF.
     */
    pubblico OutputProfile getUsedTarget() {
        restituisci usedTarget;
    }

    /**
     * Eseguire la verifica preliminare e impostare lo stato e il messaggio di conseguenza.
     */
    pubblico void run() {
        se (stato != STATO_NUOVO) {
            throw new IllegalStateException("Già eseguito");
        }
        retainedTargets.removeAll(allowedTargets);

        // Passaggio 1. Profilare il PDF. Se abbiamo impostato un elenco di profili "target" consentiti, vedere se
        // corrisponde a uno qualsiasi di questi. Se lo fa, non dobbiamo fare nulla: il PDF è già valido.
        Profilo di OutputProfile profilo = profiler.getProfile();
        Raccolta<OutputProfile> claimsTargets = profile.getClaimedTargetProfiles();

        // Scegli un target dal nostro elenco consentito
        usedTarget = defaultTarget;
        per (OutputProfile p : claimsTargets) {
            se (allowedTargets.contains(p)) {
                utilizzatoTarget = p;
                rottura;
            }
        }
        // Controlla se è già valido.
        se (profile.isCompatibleWith(usedTarget) == null) {
            stato = STATO_VALIDO;
            ritorno;
        }

        // Manteniamo tutti gli obiettivi aggiuntivi che abbiamo già raggiunto, a patto che siano
        // compatibile con il target scelto e presente nell'elenco consentito.
        Destinazione OutputProfile = nuovo OutputProfile(usedTarget);
        per (OutputProfile p : claimsTargets) {
            booleano conserva = allowedTargets.contains(p);
            se (retainedTargets.contains(p) && profile.isCompatibleWith(p) == null) {
                Tentativo {
                    target.merge(p, profilo);
                    conserva = vero;
                } cattura (ProfileComplianceException e) {
                    // Questa combinazione non è consentita.
                }
            }
            se (!mantieni) {
                // Rimuovi dal PDF qualsiasi richiesta che non possiamo soddisfare. Non strettamente
                // richiesto, ma non dovremmo scrivere un PDF che dichiara la conformità
                // (ad esempio) a PDF/UA-1 se sappiamo che il PDF non è conforme.
                // System.out.println("Rimozione del claim \"" + p.getProfileName() + "\"");
                target.negareClaim(p);
            }
        }

        // Ora proviamo a determinare il ColorSpace predefinito per il PDF, ovvero l'"intento di output".
        // che non determinerà solo la colorimetria per tutti i colori dipendenti dal dispositivo nel PDF,
        // ma influenzerà anche il modo in cui il PDF viene visualizzato in Acrobat. In generale l'approccio è:
        // 1. Se il PDF ne specifica uno, prova a riutilizzarlo.
        // 2. Altrimenti prova a indovinare se il PDF è per la maggior parte stampato o per la maggior parte visualizzato sullo schermo e scegli
        // un profilo CMYK o RGB a seconda dei casi.

        se (intentcs != null) {
            // Test OutputIntent 1: se è stato specificato esplicitamente un ColorSpace valido per OutputIntent, utilizzarlo.
            OutputIntent intent = new OutputIntent("GTS_PDFA1", null, intentcs);
            se (intent.isCompatibleWith(target) == null) {
                target.getOutputIntents().add(intento);
            }
        }
        se (target.getOutputIntent("GTS_PDFA1") == null) {
            // Test OutputIntent 2: in caso contrario, se nel PDF esiste un OutputIntent PDF/A valido, utilizzarlo.
            OutputIntent intent = profile.getOutputIntent("GTS_PDFA1");
            se (intento != null && intento.isCompatibleWith(target) == null) {
                target.getOutputIntents().add(new OutputIntent(intento));
            }
        }
        per (OutputIntent intento: profile.getOutputIntents()) {
            // OutputIntent Test 3: in caso contrario, se nel PDF esiste un altro OutputIntent valido, utilizzarlo
            // per PDF/A. Se il PDF è PDF/X e vogliamo mantenerlo così, usiamolo anche per quello.
            se (intent.isCompatibleWith(target) == null) {
                se (target.getRequiredOutputIntentTypes().contains(intent.getType())) {
                    target.getOutputIntents().add(new OutputIntent(intento));
                }
                se (target.getOutputIntent("GTS_PDFA1") == null) {
                    target.getOutputIntents().add(new OutputIntent("GTS_PDFA1", intent));
                }
            }
        }

        // Trasferiamo tutti i ColorSpaces che ci sono stati forniti da utilizzare in una nuova ProcessColorAction.
        Elenco<ColorSpace> cslist = new ArrayList<ColorSpace>();
        cslist.addAll(profile.getICCColorSpaces()); // Aggiungi tutti gli spazi colore ICC utilizzati ovunque nel PDF
        cslist.addAll(spazicolore);
        OutputProfiler.ProcessColorAction colorAction = nuovo OutputProfiler.ProcessColorAction(destinazione, cslist);
        se (target.getOutputIntent("GTS_PDFA1") == null) {
            // OutputIntent Test 4: non ne abbiamo ancora uno. Scegli tra CMYK o RGB
            // ColorSpace dall'elenco che ci è stato fornito: ma quale?
            // Se il PDF sembra pensato per la stampa, l'utilizzo della modalità CMYK darà risultati migliori.
            // Quindi se il PDF ha una separazione dei colori denominata "Ciano", "Magenta" o "Giallo", oppure
            // utilizza una modalità di fusione CMYK oppure utilizza la sovrastampa, preferendo un profilo CMYK.
            booleano cmyk = profile.isSet(OutputProfile.Feature.ColorSpaceDeviceCMYK) ||
                           profilo.isSet(OutputProfile.Feature.AnnotationColorSpaceDeviceCMYK) ||
                           profilo.isSet(OutputProfile.Feature.NChannelProcessDeviceCMYK) ||
                           profilo.isSet(OutputProfile.Feature.ColorSpaceDeviceCMYKInMatchingParent) ||
                           profilo.isSet(OutputProfile.Feature.TransparencyGroupCMYK) ||
                           profilo.isSet(OutputProfile.Feature.Overprint);
            per (OutputProfile.Separation s : profile.getColorSeparations()) {
                Nome stringa = s.getName();
                cmyk |= name.equals("Ciano") || name.equals("Magenta") || name.equals("Giallo");
            }
            Spazio colore cs = cmyk ? colorAction.getDeviceCMYK() : colorAction.getDeviceRGB();
            if (cs == null) { // Prima scelta non disponibile, ripiega sull'altra.
                cs = !cmyk ? colorAction.getDeviceCMYK() : colorAction.getDeviceRGB();
            }
            se (cs != null) {
                OutputIntent intent = nuovo OutputIntent("GTS_PDFA1", null, cs);
                se (intent.isCompatibleWith(target) == null) {
                    target.getOutputIntents().add(intento);
                    // Abbiamo modificato l'OutputIntent sul target, quindi ricreamo ColorAction
                    colorAction = nuovo OutputProfiler.ProcessColorAction(destinazione, cslist);
                }
            } altro {
                // Nessun OutputIntent impostato. Questo non è necessariamente fatale; se il PDF contiene già
                // solo colori calibrati (e per PDF/A-2 o successivi, nessuna sovrastampa), quindi potrebbe
                // va bene. Ma in genere, se arrivi qui hai bisogno di più ColorSpaces in cslist.
            }
        }
        // Avvisa se mancano spazi colore, perché probabilmente qualcosa andrà storto.
        se (colorAction.getDeviceCMYK() == null) {
            se (colorAction.getDeviceRGB() == null) {
                System.err.println("ATTENZIONE: ConvertToPDFA: ColorAction non ha un profilo sRGB o CMYK, la conversione potrebbe non riuscire");
            } altro {
                System.err.println("ATTENZIONE: ConvertToPDFA: ColorAction non ha un profilo CMYK, la conversione potrebbe non riuscire");
            }
        } altrimenti se (colorAction.getDeviceRGB() == null) {
            System.err.println("ATTENZIONE: ConvertToPDFA: ColorAction non ha un profilo RGB, la conversione potrebbe fallire");
        }
        profiler.setColorAction(coloreAzione);

        // Imposta i font da usare. Nota che cloniamo i font passati qui - PDF
        // non dovrebbe condividere i font! Clona con "new OpenTypeFont(font)"
        Il nuovo OutputProfiler AutoEmbeddingFontAction è il seguente:
        per (OpenTypeFont f : elencofont) {
            fontAction.add(nuovo OpenTypeFont(f));
        }
        // Questa riga successiva è ciò che supponiamo tu voglia: "risolvilo e basta". Altre strategie
        // sono disponibili, ma inevitabilmente causeranno più errori di conversione. Vedere l'API
        // docs per i dettagli.
        {NS} Il profilo utente di Profiler.SetFontAction(fontAction);
// profiler.setRasterizingActionExecutorService(Executors.newFixedThreadPool(3));
        profiler.setRasterizingAction(nuovo OutputProfiler.RasterizingAction() {
            // Lo sovrascriviamo solo per poterne ottenere un po' di disconnessione. Normalmente non è necessario
            @Override public void rasterize(OutputProfiler profiler, PDFPage pagina, OutputProfile pageProfile, ProfileComplianceException e) {
                stato = STATO_FISSO_BITMAP;
                messaggio = e == null ? "Rasterizzato": "Rasterizzato a causa di " + e.getFeature().getFieldName();
                super.rasterize(profiler, pagina, pageProfile, e);
            }
        });
        // Configurazione completata. Applica il profilo di destinazione.
        Tentativo {
            stato = STATO_FISSO;
            destinazione = modificaTarget(destinazione);
            profiler.apply(destinazione);
        } cattura (RuntimeException e) {
            stato = STATO_FALLITO;
            messaggio = "Non riuscito: " + e.getMessage();
            lanciare e;
        }
        // Infine, ricontrolla quale target è stato effettivamente utilizzato. "AutoConformance"
        // la strategia può modificare un target richiesto da PDF/A-2a a PDF/A-2b, ad esempio.
        // Quindi chiedi al Profiler quale ha usato.
        // getProfile() verrà restituito immediatamente qui, è già calcolato.
        profilo = profiler.getProfile();
        booleano trovato = falso;
        per (OutputProfile p : profilo.getClaimedTargetProfiles()) {
            se (p == defaultTarget || allowedTargets.contains(p)) {
                utilizzatoTarget = p;
                trovato = vero;
                rottura;
            }
        }
        se trovato) {
            // Se non abbiamo trovato quale target abbiamo utilizzato nell'elenco dei "target rivendicati",
            // è perché è stato passato un qualche tipo di target personalizzato. Il migliore
            // quello che possiamo fare qui è restituire il target effettivo che è stato utilizzato.
            usedTarget = destinazione;
        }
    }

    /**
     * Questo metodo è il modo migliore per modificare l'OutputProfile di destinazione.
     * Ad esempio, se si desidera rimuovere la compressione o stampare in modo gradevole l'XMP
     * una volta salvato, ecco dove farlo.
     * Questo metodo dovrebbe restituire il profilo modificato, in genere ciò avviene
     * essere il profilo che viene passato. L'implementazione predefinita è semplicemente
     * restituisce "target" senza modifiche.
     * @param target l'OutputProfile da modificare e restituire
     * @restituisce il profilo di destinazione da utilizzare
     */
    protetto OutputProfile modificaTarget(OutputProfile destinazione) {
        // ad esempio target.setRequired(OutputProfile.Feature.XMPMetaDataPretty);
        // ad esempio target.setDenied(OutputProfile.Feature.RegularCompression);
        obiettivo di ritorno;
    }

    /**
     * Un metodo di supporto per verificare rapidamente un PDF rispetto a un OutputProfile specificato.
     * Restituisce null se il PDF è valido e corrisponde, altrimenti restituisce un elenco di
     * motivi per cui il PDF non è stato verificato ("+" significa che la funzionalità era richiesta
     * ma mancante, "-" significa che la funzionalità è stata impostata ma non consentita)
     * @param pdf il PDF, che dovrebbe essere appena caricato
     * @param indica l'OutputProfile da verificare.
     */
    pubblico statico String verify(PDF pdf, destinazione OutputProfile) {
        Profilo di OutputProfiler = nuovo OutputProfiler(nuovo PDFParser(pdf));
        Profilo di OutputProfile profilo = profiler.getProfile();
        OutputProfile.Feature[] non corrisponde = profile.isCompatibleWith(target);
        se (mancata corrispondenza == null) {
            restituisci null;
        } altro {
            StringBuilder sb = nuovo StringBuilder();
            sb.append("[ERRORE] Verifica non riuscita su ");
            sb.append(target.getProfileName());
            aggiungi(":");
            per (OutputProfile.Feature f : non corrispondenza) {
                sb.append(target.isRequired(f) ? " -" : " +");
                sb.append(f.toString());
            }
            restituisci sb.toString();
        }
    }

    // ------------------------------------------------ ---------------------

    privato static void help() {
        Sistema.out.println();
        System.out.println("java ConvertToPDFA [--font <file>]+ [--icc <file>]+ --<nomeprofilo> [<file.pdf>+ | --files-from <file>]");
        System.out.println(" --font <fontfile> uno o più font OpenType da considerare per la sostituzione nel PDF");
        System.out.println(" --icc srgb|<iccfile> uno o più profili ICC per calibrare il colore del PDF");
        System.out.println(" --<profilename> quali profili PDF/A scegliere come target. Le opzioni valide includono pdfa1, pdfa2, pdfa3");
        {NS} = ...
        System.out.println(" --files-from <file> specifica un file contenente l'elenco dei file PDF da elaborare, diversi da");
        System.out.println(" elencandoli tutti sulla riga di comando.");
        Sistema.out.println();
        System.out.println(" Per un corretto funzionamento, è necessario fornire almeno due profili ICC: uno RGB e uno CMYK. Il");
        System.out.println(" CMYK deve essere caricato da un file profilo ICC, ma il valore \"srgb\" può essere utilizzato per caricare sRGB");
        System.out.println(" profile. Dovrebbero essere forniti più font; consigliamo almeno un font NotoSansCJK e, idealmente,");
        System.out.println(" i font Times, Arial e Courier forniti con Windows. Non dimenticare le varianti grassetto e corsivo.");
        Sistema.out.println();
        System.out.println("I PDF verranno verificati o convertiti in base ai profili PDF/A specificati, con il primo come predefinito");
        System.out.println(" scelta. Se non viene specificato alcun profilo, viene accettato qualsiasi profilo PDF/A, con PDF/A-1 come predefinito");
        Sistema.out.println();
    }

    pubblico static void main(String[] args) genera eccezione {
        Elenco<OpenTypeFont> caratteri = nuovo ArrayList<OpenTypeFont>();
        Elenco<OutputProfile> destinazioni = nuovo ArrayList<OutputProfile>();
        Elenco<ColorSpace> spazi colore = nuovo ArrayList<ColorSpace>();
        Elenco<Stringa> nomi file = nuovo ArrayList<Stringa>();
        Intento ColorSpace = null;

        se (arg.lunghezza == 0) {
            aiuto();
            Sistema.exit(1);
        }
        per (int i=0;i<lunghezzaarg;i++) {
            Stringa s = args[i];
            se (s.equals("--help")) {
                aiuto();
                Sistema.exit(1);
            } altrimenti se (s.equals("--font")) {
                carattere OpenTypeFont = nuovo OpenTypeFont(nuovo File(args[++i]), null);
                font.add(carattere);
            } altrimenti se (s.equals("--icc")) {
                Nome stringa = argomenti[++i];
                se (nome.equalsIgnoreCase("srgb")) {
                    spazicolori.add(Color.red.getColorSpace());
                } altro {
                    colorspaces.add(new ICCColorSpace(new File(name)));
                }
            } altrimenti se (s.equals("--icc-intent")) {
                Nome stringa = argomenti[++i];
                se (nome.equalsIgnoreCase("srgb")) {
                    spazicolori.add(Color.red.getColorSpace());
                } altro {
                    colorspaces.add(new ICCColorSpace(new File(name)));
                }
                intentcs = colorspaces.get(colorspaces.size() - 1);
            } altrimenti se (s.equals("--pdfa1")) {
                obiettivi.add(OutputProfile.PDFA1a_2005);
                obiettivi.add(OutputProfile.PDFA1b_2005);
            } altrimenti se (s.equals("--pdfa2")) {
                obiettivi.add(OutputProfile.PDFA2a);
                obiettivi.add(OutputProfile.PDFA2b);
                obiettivi.add(OutputProfile.PDFA2u);
            } altrimenti se (s.equals("--pdfa3")) {
                obiettivi.add(OutputProfile.PDFA3a);
                obiettivi.add(OutputProfile.PDFA3b);
                obiettivi.add(OutputProfile.PDFA3u);
            } altrimenti se (s.equals("--pdfa4")) {
                obiettivi.add(OutputProfile.PDFA4);
                obiettivi.add(OutputProfile.PDFA4e);
                obiettivi.add(OutputProfile.PDFA4f);
            } altrimenti se (s.equals("--files-from")) {
                BufferedReader r = nuovo BufferedReader(nuovo InputStreamReader(nuovo FileInputStream(args[++i]), "UTF-8"));
                mentre ((s=r.readLine()) != null) {
                    nomifile.aggiungi;
                }
                r.chiudi();
            } altro {
                nomifile.aggiungi;
            }
            per (Stringa nomefile: nomifile) {
                File infile = nuovo File(nomefile);
                PDF pdf = nullo;
                Tentativo {
                    pdf = nuovo PDF(nuovo PDFReader(infile));
                } catch (Eccezione e) {
                    // Il PDF non è riuscito a caricarsi completamente, forse è gravemente danneggiato,
                    // protetto da password o no è un PDF? Segnalalo e continua.
                    System.out.println(infile + " [ERRORE]: " + e);
                }
                se (pdf != null) {
                    Tentativo {
                        // Convertire il PDF in PDF/A
                        Preflight ConvertToPDFA = nuovo ConvertToPDFA(pdf);
                        se (obiettivi.isEmpty()) {
                            // Se non sono specificati profili di destinazione, seleziona qualsiasi profilo PDF/A pubblicato
                            // e PDF/A-1b per impostazione predefinita.
                            preflight.setDefaultTarget(OutputProfile.PDFA1b_2005);
                            preflight.setTargetProfiles(Array.asList(
                                Profilo di output.PDFA1b_2005, Profilo di output.PDFA1a_2005,
                                Profilo di output.PDFA2a, Profilo di output.PDFA2b, Profilo di output.PDFA2u,
                                Profilo di output.PDFA3a, Profilo di output.PDFA3u, Profilo di output.PDFA3b,
                                Profilo di output.PDFA4, Profilo di output.PDFA4e, Profilo di output.PDFA4f));
                        } altro {
                            preflight.setDefaultTarget(obiettivi.get(0));
                            preflight.setTargetProfiles(obiettivi);
                        }
                        preflight.setFontList(caratteri);
                        se (colorspaces.isEmpty()) {
                            // Tecnicamente non è fatale, ma significa che non hai specificato
                            // il parametro "--icc" - due volte - per specificare un profilo RGB e CMYK.
                            // Probabilmente si tratta di una svista, quindi genera un'eccezione.
                            throw new IllegalStateException("Nessun ColorSpace specificato! La conversione richiede quasi sempre di specificare uno spazio colore RGB e CMYK");
                        }
                        preflight.setColorSpaces(spazi colore);
                        preflight.setOutputIntentColorSpace(intentcs);
                        preflight.esegui();

                        // Relazione sui risultati
                        OutputProfile utilizzatoTarget = preflight.getUsedTarget();
                        se (preflight.getState() == STATO_VALIDO) {
                            // Non sono state apportate modifiche.
                            System.out.println(infile + " [OK]: Già valido per " + usedTarget.getProfileName());
                        } altro {
                            // Sono state apportate modifiche. Salva il PDF, quindi caricalo e verificalo.
                            // Prima di salvare, aggiungiamo una voce alla "Cronologia" nei metadati
                            // annotando la conversione. Opzionale, ma i metadati sono sempre utili.
                            pdf.getXMP().addHistory("Convertito in PDF/A", null, "BFOPDF " + PDF.VERSION, null, null);
                            File outfile = nuovo File("preflight-" + infile.getName());
                            OutputStream out = nuovo FileOutputStream(outfile);
                            pdf.render(fuori);
                            fuori.chiudi();

                            Stringa messaggio = null;
                            se è vero) {
                                // Come controllo assicurativo, questo blocco ricarica il PDF che
                                // appena creato e verifica che sia davvero valido.
                                pdf = nuovo PDF(nuovo PDFReader(outfile));
                                messaggio = verifica(pdf, usedTarget);
                            }
                            se (messaggio != null) {
                                // Pensavamo che il PDF fosse stato riparato, ma non lo era. Se
                                // abbiamo svolto correttamente il nostro lavoro, questo blocco non dovrebbe funzionare.
                                System.out.println(infile + ": " + messaggio);
                            } altro {
                                // PDF verificato con successo.
                                messaggio = preflight.getMessage();
                                se (messaggio == null) {
                                    System.out.println(infile + ": [OK] Convertito in " + usedTarget.getProfileName() + " e salvato come \"" + outfile + "\"");
                                } altro {
                                    System.out.println(infile + ": [OK] Convertito in " + usedTarget.getProfileName() + " e salvato come \"" + outfile + "\" (" + message + ")");
                                }
                            }
                        }
                    } catch (Eccezione e) {
                        // Qualcosa è andato storto! Non dovresti arrivare qui, ma se
                        // esegui il log e vai al file successivo.
                        System.out.println(infile + ": [ERRORE] " + e);
                        e.printStackTrace(System.out);
                    }
                }
            }
            nomi file.clear();
        }
    }

}