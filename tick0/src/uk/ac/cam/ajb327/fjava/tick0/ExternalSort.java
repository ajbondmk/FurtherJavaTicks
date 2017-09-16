package uk.ac.cam.ajb327.fjava.tick0;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExternalSort {

	public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
		//TODO: Complete this method

		RandomAccessFile f = new RandomAccessFile("/home/arb33/example","rw");
		DataOutputStream d = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f.getFD())));
		d.writeInt(1); //write calls now only store primitive ints in memory
		d.writeInt(2);
		d.writeInt(3);
		d.flush(); //force the contents to be written to the disk. Important!
		f.seek(4);
		System.out.println("Read four bytes as an int value "+f.readInt());
		System.out.println("The file is "+f.length()+" bytes long");
	}

	private static String byteToHex(byte b) {
		String r = Integer.toHexString(b);
		if (r.length() == 8) {
			return r.substring(6);
		}
		return r;
	}

	public static String checkSum(String f) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			DigestInputStream ds = new DigestInputStream(
					new FileInputStream(f), md);
			byte[] b = new byte[512];
			while (ds.read(b) != -1)
				;

			String computed = "";
			for(byte v : md.digest()) 
				computed += byteToHex(v);

			return computed;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "<error computing checksum>";
	}

	public static void main(String[] args) throws Exception {
		String f1 = args[0];
		String f2 = args[1];
		sort(f1, f2);
		System.out.println("The checksum is: " + checkSum(f1));
	}
}
