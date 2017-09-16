package uk.ac.cam.ajb327.fjava.tick0;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExternalSort {

	public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
		//TODO: Complete this method
	}

	private static void testReadWrite() throws IOException {
		DataOutputStream dOut = getOutputStream();
		dOut.writeInt(1);
		dOut.writeInt(2);
		dOut.writeInt(3);
		dOut.flush();
		dOut.close();
		//f.seek(4);
		//System.out.println("Read four bytes as an int value " + f.readInt());
		//System.out.println("The file is " + f.length() + " bytes long");
		printFile(getInputStream());
	}

	private static DataOutputStream getOutputStream() throws IOException {
		return new DataOutputStream(
			new BufferedOutputStream(
				new FileOutputStream(
					new RandomAccessFile("./outputs/example2","rw").getFD()
				)
			)
		);
	}

	private static DataInputStream getInputStream() throws IOException {
		return new DataInputStream(
			new BufferedInputStream(
				new FileInputStream(
					new RandomAccessFile("./outputs/example2","rw").getFD()
				)
			)
		);
	}

	private static void printFile(DataInputStream dIn) {
		try {
			System.out.println("sizeing: " + dIn.available());
			while (dIn.available() > 0) {
				System.out.println("printing: " + dIn.readInt());
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
		System.out.println("The checksum for " + f1 + " is: " + checkSum(f1));
	}
}
