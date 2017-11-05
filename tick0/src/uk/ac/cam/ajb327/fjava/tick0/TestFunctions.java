package uk.ac.cam.ajb327.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

// This class contains functions written to help debug

public class TestFunctions {

	public static void mainTest() throws IOException {
		long startTime = System.nanoTime();
		for (int testNum = 1; testNum <= 17; testNum++) {
			String f1 = "test-suite/test" + testNum + "a.dat";
			String f2 = "test-suite/test" + testNum + "b.dat";
			ExternalSort.sort(f1, f2);
			TestFunctions.checkCheckSum(testNum, f1);
		}
		long endTime = System.nanoTime();
		long timeTaken = endTime - startTime;
		System.out.println();
		System.out.println("Total time taken: " + timeTaken / 1000000 + "ms");
	}

	public static void checkCheckSum(int testNum, String file) throws IOException {
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
		String fileChecksum = ExternalSort.checkSum(file);
		System.out.print("Test file " + testNum);
		System.out.println(fileChecksum.equals(correctChecksums[testNum-1]) ? " passed!" : " failed.");
	}

	public static void printFile(String file) {
		try {
			DataInputStream dIn = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			while (dIn.available() > 0) {
				System.out.print(dIn.readInt() + " ");
			}
			System.out.println();
		} catch (IOException err) {
			System.out.println(err.getMessage());
		}
	}

	public static void printFile2(String file, int fileLength) {
		try {
			System.out.println("Start of thread print");
			DataInputStream dIn = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			int nextItem = dIn.readInt();
			int currentValue = nextItem;
			int count = 1;
			int drops = 0;
			for (int i = 1; i < fileLength / 4; i++) {
				nextItem = dIn.readInt();
				if (currentValue == nextItem) count++;
				else {
					System.out.print(currentValue + " - " + count);
					if (nextItem < currentValue) {
						drops++;
						System.out.print(" " + drops + " drop(s)");
					}
					System.out.println();
					currentValue = nextItem;
					count = 1;
				}
			}
			System.out.println(currentValue + " - " + count);
			System.out.println("End of thread, " + drops + " drops");
			System.out.println();
		} catch (IOException err) {
			System.out.println(err.getMessage());
		}
	}

	public static void checkIfSorted(String file, int fileLength) throws IOException {
		DataInputStream dIn = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		int previous = Integer.MIN_VALUE;
		for (int i = 0; i < fileLength / 4; i++) {
			int current = dIn.readInt();
			if (current < previous) {
				System.out.println("NOT SORTED");
				System.out.println("Previous: " + previous);
				System.out.println("Current:  " + current);
				System.out.println("Current position: " + i*4);
				System.out.println("File length: " + fileLength);
				System.out.println();
			}
			previous = current;
		}
		System.out.println("End of sort check.");
		System.out.println();
	}

}
