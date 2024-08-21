//package com.example.decrypt;

//import com.example.decrypt.service.JsonDecryptionService;
//import com.example.decrypt.util.CryptoUtils;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
package it.cript.utility;

import org.springframework.stereotype.Service;

@Service
public class DecryptApplication {

	public static  String decrypt(String decryptionKey ,String metadata) throws Exception {		
		String json = metadata;
				 // Generare una chiave valida
	//	String decryptionKey = CryptoUtils.generateKey();		
	//	System.out.println("Genero la Key a 256 : " + decryptionKey);//Stampa la key		
	//	decryptionKey=decryptionKey+"***123***";//Modifico per generare errore.		
	//	System.out.println(decryptionKey);		
		//Con la key generata cripto il json 
		String encryptedJson = CryptoUtils.decrypt(json, decryptionKey);
		
		System.out.println("Stampo il JSON decriptato: " + encryptedJson+"\n");//Stampa il Json

		//
		boolean condition = true;
		String result = JsonDecryptionService.processEncryptedJson(encryptedJson, condition, decryptionKey);
		System.out.println("Stampo il JSON decriptato : "+"\n" + result);
		return encryptedJson;
	}

}
