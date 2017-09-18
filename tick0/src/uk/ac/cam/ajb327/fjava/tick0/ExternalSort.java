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

		int length = fileLength(f1);
		boolean readingFromF1 = true;
		for (int chunkSize = 4; chunkSize < length; chunkSize *= 2) {
			int lengthLeft = length;
			DataInputStream dIn1 = getInputStream(readingFromF1 ? f1 : f2);
			DataInputStream dIn2 = getInputStream(readingFromF1 ? f1 : f2);
			dIn2.skip(chunkSize);
			DataOutputStream dOut = getOutputStream(readingFromF1 ? f2 : f1);
			while (true) {
				if (lengthLeft > chunkSize) {
					int leftIn1 = chunkSize;
					int leftIn2 = (lengthLeft >= 2*chunkSize) ? chunkSize : lengthLeft-chunkSize;
					int current1 = dIn1.readInt();
					int current2 = dIn2.readInt();
					while (true) {
						if (leftIn1 == 0 && leftIn2 == 0) {
							break;
						} else if ((current1 < current2 && leftIn1 != 0) || leftIn2 == 0) {
							dOut.writeInt(current1);
							leftIn1 -= 4;
							if (leftIn1 != 0) current1 = dIn1.readInt();
						} else {
							dOut.writeInt(current2);
							leftIn2 -= 4;
							if (leftIn2 != 0) current2 = dIn2.readInt();
						}
					}
					dIn1.skip(chunkSize);
					dIn2.skip(chunkSize);
					lengthLeft -= 2*chunkSize;
				} else {
					while (lengthLeft > 0) {
						dOut.writeInt(dIn1.readInt());
						lengthLeft -= 4;
					}
					break;
				}
			}

			dOut.flush();
			dIn1.close();
			dIn2.close();
			dOut.close();
			readingFromF1 = !readingFromF1;
		}
		if (!readingFromF1) {
			DataInputStream dIn = getInputStream(f2);
			DataOutputStream dOut = getOutputStream(f1);
			while (dIn.available() > 0) {
				dOut.writeInt(dIn.readInt());
			}
			dOut.flush();
			dIn.close();
			dOut.close();
		}
	}

	private static int fileLength(String file) throws IOException {
		DataInputStream fileIn = getInputStream(file);
		int length = fileIn.available();
		fileIn.close();
		return length;
	}

	private static DataOutputStream getOutputStream(String location) throws IOException {
		return new DataOutputStream(
			new BufferedOutputStream(
				new FileOutputStream(
					new RandomAccessFile(location,"rw").getFD()
				)
			)
		);
	}

	private static DataInputStream getInputStream(String location) throws IOException {
		return new DataInputStream(
			new BufferedInputStream(
				new FileInputStream(
					new RandomAccessFile(location,"rw").getFD()
				)
			)
		);
	}

	private static void printFile(DataInputStream dIn) {
		try {
			while (dIn.available() > 0) {
				System.out.print(dIn.readInt() + " ");
			}
			System.out.println();
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

		for (int testNum = 1; testNum <= 17; testNum++) {
			String f1 = "test-suite/test" + testNum + "a.dat";
			String f2 = "test-suite/test" + testNum + "b.dat";
			sort(f1, f2);
			checkCheckSum(testNum, f1);
		}
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
		System.out.print(fileChecksum.equals(correctChecksums[testNum-1]) ? " passed!" : " failed.");
		System.out.println();
	}
}
