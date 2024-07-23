package it.pdf;

/*
2	 * Licensed to the Apache Software Foundation (ASF) under one or more
3	 * contributor license agreements.  See the NOTICE file distributed with
4	 * this work for additional information regarding copyright ownership.
5	 * The ASF licenses this file to You under the Apache License, Version 2.0
6	 * (the "License"); you may not use this file except in compliance with
7	 * the License.  You may obtain a copy of the License at
8	 *
9	 *      http://www.apache.org/licenses/LICENSE-2.0
10	 *
11	 * Unless required by applicable law or agreed to in writing, software
12	 * distributed under the License is distributed on an "AS IS" BASIS,
13	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
14	 * See the License for the specific language governing permissions and
15	 * limitations under the License.
16	 */

	
	import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerException;

import org.apache.pdfbox.pdfwriter.compress.CompressParameters;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

	/**
	 * Creates a simple PDF/A document.
	 */
	public final class CreatePDFA
	{
	    private CreatePDFA()
	    {
	    }
	    
	    public static void main(String[] args) throws IOException, TransformerException
	    {
	        if (args.length != 3)
	        {
	            System.err.println("usage: " + CreatePDFA.class.getName() +
	                    " <output-file> <Message> <ttf-file>");
	            System.exit(1);
	        }
	
	        String file = args[0];
	        String message = args[1];
	        String fontfile = args[2];
	
	        try (PDDocument doc = new PDDocument())
	        {
	            PDPage page = new PDPage();
	            doc.addPage(page);
	
	            // load the font as this needs to be embedded
	            PDFont font = PDType0Font.load(doc, new File(fontfile));
	
	            // A PDF/A file needs to have the font embedded if the font is used for text rendering
                // in rendering modes other than text rendering mode 3.
	            //
	            // This requirement includes the PDF standard fonts, so don't use their static PDFType1Font classes such as
	            // PDFType1Font.HELVETICA.
	            //
	            // As there are many different font licenses it is up to the developer to check if the license terms for the
	            // font loaded allows embedding in the PDF.
	            // 
	            if (!font.isEmbedded())
	            {
	                throw new IllegalStateException("PDF/A compliance requires that all fonts used for"
	                                + " text rendering in rendering modes other than rendering mode 3 are embedded.");
	            }
	            
	            // create a page with the message
	            try (PDPageContentStream contents = new PDPageContentStream(doc, page))
	            {
	                contents.beginText();
	                contents.setFont(font, 12);
	                contents.newLineAtOffset(100, 700);
	                contents.showText(message);
	                contents.endText();
92	            }
93	
94	            // add XMP metadata
95	            XMPMetadata xmp = XMPMetadata.createXMPMetadata();
96	            
97	            try
98	            {
99	                DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
100	                dc.setTitle(file);
101	                
102	                PDFAIdentificationSchema id = xmp.createAndAddPDFAIdentificationSchema();
103	                id.setPart(1);
104	                id.setConformance("B");
105	                
106	                XmpSerializer serializer = new XmpSerializer();
107	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
108	                serializer.serialize(xmp, baos, true);
109	
110	                PDMetadata metadata = new PDMetadata(doc);
111	                metadata.importXMPMetadata(baos.toByteArray());
112	                doc.getDocumentCatalog().setMetadata(metadata);
113	            }
114	            catch(BadFieldValueException e)
115	            {
116	                // won't happen here, as the provided value is valid
117	                throw new IllegalArgumentException(e);
118	            }
119	
120	            // sRGB output intent
121	            InputStream colorProfile = CreatePDFA.class.getResourceAsStream(
122	                    "/org/apache/pdfbox/resources/pdfa/sRGB.icc");
123	            PDOutputIntent intent = new PDOutputIntent(doc, colorProfile);
124	            intent.setInfo("sRGB IEC61966-2.1");
125	            intent.setOutputCondition("sRGB IEC61966-2.1");
126	            intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
127	            intent.setRegistryName("http://www.color.org");
128	            doc.getDocumentCatalog().addOutputIntent(intent);
129	
130	            doc.save(file, CompressParameters.NO_COMPRESSION);
131	        }
132	    }
133	}
