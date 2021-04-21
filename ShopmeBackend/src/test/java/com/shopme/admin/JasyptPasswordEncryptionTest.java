package com.shopme.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;

//import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
//import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

public class JasyptPasswordEncryptionTest {

	@Test
	public void testEncryptPassword() {
		
//	    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//	    encryptor.setPassword("password");
//	    encryptor.setPoolSize(1);
//	    
//		String rawPassword = "root";
//		String encryptedPassword = encryptor.encrypt(rawPassword);
//		
//		System.out.println(encryptedPassword);
//		
//		String decryptedPassword = encryptor.decrypt(encryptedPassword);
//		
//		assertEquals("password", decryptedPassword);
	}
	
	@Test
	public void testEncryptPasswordAdvancedConfig() {
		
//	    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//	    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//	    config.setPassword("password");
//	    config.setAlgorithm("PBEWithMD5AndDES");
//	    config.setKeyObtentionIterations("1000");
//	    config.setPoolSize("1");
//	    config.setProviderName("SunJCE");
//	    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//	    config.setStringOutputType("base64");
//	    encryptor.setConfig(config);
//	    
//		String rawPassword = "password";
//		String encryptedPassword = encryptor.encrypt(rawPassword);
//		
//		System.out.println(encryptedPassword);
//		
//		String decryptedPassword = encryptor.decrypt(encryptedPassword);
//		
//		assertEquals("password", decryptedPassword);
	}	
}
