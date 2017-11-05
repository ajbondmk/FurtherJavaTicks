package uk.ac.cam.ajb327.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExternalSort {

	public static void sort(String f1, String f2) throws FileNotFoundException, IOException {

		//Find the size of the file
		FileInputStream fIn = new FileInputStream(f1);
		final int FILE_SIZE = fIn.available();
		fIn.close();

		//Choose how many integers to sort in the initial in-memory sort
		int INITIAL_SORT_SIZE = 1048576;

		//Initial in-memory sort, sizes are in terms of ints
		initialSort(f1, f2, FILE_SIZE / 4, INITIAL_SORT_SIZE / 4);

		//Merge sort of sorted chunks, sizes are in terms of bytes
		mergeSort(f1, f2, FILE_SIZE, INITIAL_SORT_SIZE);

	}

	private static DataOutputStream getOutputStream(String location) throws IOException {
		//Return a new buffered output stream capable of writing ints efficiently to a chosen file
		return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(location)));
	}

	private static DataInputStream getInputStream(String location) throws IOException {
		//Return a new buffered input stream capable of reading ints efficiently from a chosen file
		return new DataInputStream(new BufferedInputStream(new FileInputStream(location)));
	}

	private static void initialSort(String f1, String f2, final int FILE_SIZE_INTS, final int INITIAL_SORT_SIZE_INTS) throws IOException {

		//Chunk of ints copied into memory to be sorted
		List<Integer> chunkToSort = new ArrayList<>();

		DataInputStream dIn = getInputStream(f1);
		DataOutputStream dOut = getOutputStream(f2);

		//Iterate through dIn, reading into memory
		//Once enough ints are in memory, sort them and write to dOut, then clear memory
		for (int num = 1; num <= FILE_SIZE_INTS; num++) {
			chunkToSort.add(dIn.readInt());
			boolean endOfChunk = (num % INITIAL_SORT_SIZE_INTS == 0);
			boolean endOfFile = (num == FILE_SIZE_INTS);
			if (endOfChunk || endOfFile) {
				Collections.sort(chunkToSort);
				for (Integer i : chunkToSort) {
					dOut.writeInt(i);
				}
				chunkToSort.clear();
				if (endOfFile) break;
			}
		}

		dIn.close();
		dOut.close();

	}

	private static void mergeSort(String f1, String f2, final int FILE_SIZE, final int INITIAL_SORT_SIZE) throws IOException {

		//Keeps track of which file is being read from
		boolean readingFromF1 = false;

		//Starts with chunks already in-memory sorted, then doubles from there
		for (int chunkSize = INITIAL_SORT_SIZE; chunkSize < FILE_SIZE; chunkSize *= 2) {

			int lengthLeft = FILE_SIZE;

			//Two input streams for the two chunks that are to be merged
			DataInputStream dIn1 = getInputStream(readingFromF1 ? f1 : f2);
			DataInputStream dIn2 = getInputStream(readingFromF1 ? f1 : f2);
			dIn2.skip(chunkSize);

			DataOutputStream dOut = getOutputStream(readingFromF1 ? f2 : f1);

			while (true) {

				//There are two chunks to merge
				if (lengthLeft > chunkSize) {

					//Keep track of how many bytes are left in each chunk
					int leftIn1 = chunkSize;
					int leftIn2 = (lengthLeft >= 2*chunkSize) ? chunkSize : (lengthLeft-chunkSize);

					//Keep track of the head of each chunk being merged
					int current1 = dIn1.readInt();
					int current2 = dIn2.readInt();

					//While there are numbers left in at least one of the two chunks
					while (leftIn1 > 0 || leftIn2 > 0) {
						if ((current1 < current2 && leftIn1 > 0) || leftIn2 == 0) {
							//The next number to output is in the first chunk
							dOut.writeInt(current1);
							leftIn1 -= 4;
							if (leftIn1 > 0) current1 = dIn1.readInt();
						} else {
							//The next number to output is in the second chunk
							dOut.writeInt(current2);
							leftIn2 -= 4;
							if (leftIn2 > 0) current2 = dIn2.readInt();
						}
					}

					//Move inputs onto the next pair of chunks
					dIn1.skip(chunkSize);
					dIn2.skip(chunkSize);

					lengthLeft -= 2 * chunkSize;

				} else {

					//There is only one chunk left in the file, so copy the rest of it to output
					while (lengthLeft > 0) {
						dOut.writeInt(dIn1.readInt());
						lengthLeft -= 4;
					}

					//Once this is done, the file is empty, so break out of the while loop
					break;

				}

			}

			dIn1.close();
			dIn2.close();
			dOut.close();

			//Switch which file is being read from
			readingFromF1 = !readingFromF1;

		}

		//If the sort is complete but in f2, copy it to f1
		if (!readingFromF1) {
			Files.copy(Paths.get(f2), Paths.get(f1), StandardCopyOption.REPLACE_EXISTING);
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
//		String f1 = args[0];
//		String f2 = args[1];
//		sort(f1, f2);
//		System.out.println("The checksum is: " + checkSum(f1));
		TestFunctions.mainTest();
	}

}
