package uk.ac.cam.ajb327.fjava.tick0;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExternalSort {

	public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
		RandomAccessFile f = new RandomAccessFile("./outputs/example2", "rw");
		DataOutputStream dOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f.getFD())));
		dOut.writeInt(2);
		dOut.writeInt(1);
		dOut.flush();
		printFile(f);
	}

	private static void printFile(RandomAccessFile f) {
		try {
			DataInputStream d = new DataInputStream(new BufferedInputStream(new FileInputStream(f.getFD())));
			while (d.available() > 0) {
				System.out.println(d.readInt());
			}
		} catch (IOException err) {
			System.out.println(err.getMessage());
		}
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

		//NEW
		System.out.println("The checksum should be: 1e52ef3b2acef1f831f728dc2d16174d7");
		if (checkSum(f1) == "1e52ef3b2acef1f831f728dc2d16174d7") {
			System.out.println("Test passed!");
		} else {
			System.out.println("Test failed.");
		}
	}

}
