package uk.ac.cam.ajb327.fjava.tick0;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExternalSort {

	public static void sort(String f1, String f2) throws FileNotFoundException, IOException {

		FileInputStream fIn = new FileInputStream(f1);
		int fileLength = fIn.available();
		fIn.close();

		int initialSortInts = 262144;

		Sorter s = new Sorter(f1, f2, fileLength, initialSortInts);
		s.sort();

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
//		String f1 = args[0];
//		String f2 = args[1];
//		sort(f1, f2);
//		System.out.println("The checksum is: " + checkSum(f1));
		
		long startTime = System.nanoTime();
		for (int testNum = 1; testNum <= 17; testNum++) {
			String f1 = "test-suite/test" + testNum + "a.dat";
			String f2 = "test-suite/test" + testNum + "b.dat";
			sort(f1, f2);
			checkCheckSum(testNum, f1);
		}
		long endTime = System.nanoTime();
		long timeTaken = endTime - startTime;
		System.out.println();
		System.out.println("Total time taken: " + timeTaken / 1000000 + "ms");
	}

	private static void checkCheckSum(int testNum, String file) throws IOException {
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
		String fileChecksum = checkSum(file);
		System.out.print("Test file " + testNum);
		System.out.println(fileChecksum.equals(correctChecksums[testNum-1]) ? " passed!" : " failed.");
	}

	@SuppressWarnings("Duplicates")
	private static void printFile(String f) {
		try {
			DataInputStream dIn = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
			while (dIn.available() > 0) {
				System.out.print(dIn.readInt() + " ");
			}
			System.out.println();
		} catch (IOException err) {
			System.out.println(err.getMessage());
		}
	}

}
