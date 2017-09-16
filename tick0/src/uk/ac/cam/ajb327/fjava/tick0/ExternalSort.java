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
		//String f1 = args[0];
		//String f2 = args[1];
		//sort(f1, f2);
		//System.out.println("The checksum is: " + checkSum(f1));
		int testUpTo = 6; //HARDCODED
		for (int testNum = 1; testNum <= testUpTo; testNum++) {
			String f1 = "inputs/test" + testNum + "a.dat";
			String f2 = "inputs/test" + testNum + "b.dat";
			sort(f1, f2);
			checkChecksum(testNum);
		}
	}

	private static void checkChecksum(int testNum) {
		String[] correctChecksums = {
			"d41d8cd98f0b24e980998ecf8427e",
			"a54f041a9e15b5f25c463f1db7449",
			"c2cb56f4c5bf656faca0986e7eba38",
			"c1fa1f22fa36d331be4027e683baad6",
			"8d79cbc9a4ecdde112fc91ba625b13c2",
			"1e52ef3b2acef1f831f728dc2d16174d",
			"6b15b255d36ae9c85ccd3475ec11c3",
			"1484c15a27e48931297fb6682ff625",
			"ad4f60f065174cf4f8b15cbb1b17a1bd",
			"32446e5dd58ed5a5d7df2522f0240",
			"435fe88036417d686ad8772c86622ab",
			"c4dacdbc3c2e8ddbb94aac3115e25aa2",
			"3d5293e89244d513abdf94be643c630",
			"468c1c2b4c1b74ddd44ce2ce775fb35c",
			"79d830e4c0efa93801b5d89437f9f3e",
			"c7477d400c36fca5414e0674863ba91",
			"cc80f01b7d2d26042f3286bdeff0d9"
		};
		String f1Checksum = checkSum("test-suite/test" + testNum + "a.dat");
		System.out.println("Test file " + testNum);
		System.out.println("Calculated checksum: " + f1Checksum);
		System.out.println("Correct checksum: " + correctChecksums[testNum-1]);
		System.out.println(f1Checksum.equals(correctChecksums[testNum-1]) ? "Test passed!" : "Test failed.");
		System.out.println();
	}
}
