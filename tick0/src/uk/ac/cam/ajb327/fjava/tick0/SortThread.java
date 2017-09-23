package uk.ac.cam.ajb327.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortThread extends Thread {

	private String f2;
	private String f1;
	private int startPos;
	private int fileLength;
	private int initialSortInts;

	public static long timeSpentInitial = 0;
	public static long timeSpentMerge = 0;

	SortThread (String F1, String F2, int StartPos, int FileLength, int InitialSortInts) {
		f1 = F1;
		f2 = F2;
		startPos = StartPos;
		fileLength = FileLength;
		initialSortInts = InitialSortInts;
	}

	public void run () {
		try {
			initialSort(f1, f2, fileLength, initialSortInts);
			mergeSort(f1, f2, fileLength, initialSortInts);
		} catch (IOException e) {
			//TODO: REMOVE THIS
			System.out.println("ERROR: " + e.getMessage());
		}
	}

	private DataOutputStream getOutputStream(String location) throws IOException {
		RandomAccessFile f = new RandomAccessFile(location, "rw");
		f.seek(startPos);
		return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f.getFD())));
	}

	private DataInputStream getInputStream(String location) throws IOException {
		DataInputStream dIn = new DataInputStream(new BufferedInputStream(new FileInputStream(location)));
		dIn.skip(startPos);
		return dIn;
	}

	private void initialSort(String f1, String f2, int fileLength, int initialSortInts) throws IOException {

		long startTime = System.nanoTime();

		List<Integer> chunkToSort = new ArrayList<>();

		DataInputStream dInInitial = getInputStream(f1);
		DataOutputStream dOutInitial = getOutputStream(f2);

		int fileLengthInts = fileLength / 4;

		for (int num = 1; num <= fileLengthInts; num++) {
			chunkToSort.add(dInInitial.readInt());
			boolean endOfChunk = (num % initialSortInts == 0);
			boolean endOfFile = (num == fileLengthInts);
			if (endOfChunk || endOfFile) {
				Collections.sort(chunkToSort);
				for (Integer i : chunkToSort) {
					dOutInitial.writeInt(i);
				}
				chunkToSort.clear();
				if (endOfFile) break;
			}
		}

		dOutInitial.flush();

		dInInitial.close();
		dOutInitial.close();

		long endTime = System.nanoTime();
		timeSpentInitial += endTime - startTime;

	}

	private void mergeSort(String f1, String f2, int fileLength, int initialSortInts) throws IOException {

		long startTime = System.nanoTime();

		boolean readingFromF1 = false;

		for (int chunkSize = initialSortInts * 4; chunkSize < fileLength; chunkSize *= 2) {

			int lengthLeft = fileLength;

			DataInputStream dIn1 = getInputStream(readingFromF1 ? f1 : f2);
			DataInputStream dIn2 = getInputStream(readingFromF1 ? f1 : f2);
			DataOutputStream dOut = getOutputStream(readingFromF1 ? f2 : f1);
			dIn2.skip(chunkSize);

			while (true) {

				if (lengthLeft > chunkSize) {

					int leftIn1 = chunkSize;
					int leftIn2 = chunkSize;
					if (lengthLeft < 2 * chunkSize) {
						leftIn2 = lengthLeft - chunkSize;
					}
					int current1 = dIn1.readInt();
					int current2 = dIn2.readInt();

					while (leftIn1 > 0 || leftIn2 > 0) {
						if ((current1 < current2 && leftIn1 > 0) || leftIn2 == 0) {
							dOut.writeInt(current1);
							leftIn1 -= 4;
							if (leftIn1 > 0) current1 = dIn1.readInt();
						} else {
							dOut.writeInt(current2);
							leftIn2 -= 4;
							if (leftIn2 > 0) current2 = dIn2.readInt();
						}
					}

					dIn1.skip(chunkSize);
					dIn2.skip(chunkSize);
					lengthLeft -= 2 * chunkSize;
					if (lengthLeft < 0) lengthLeft = 0;

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

		//if (!readingFromF1) copyToF1(f1, f2, fileLength);

		long endTime = System.nanoTime();
		timeSpentMerge += endTime - startTime;

	}

	private static void copyToF1(String f1, String f2, int fileLength) throws IOException {
		FileChannel src = new FileInputStream(f2).getChannel();
		FileChannel dest = new FileOutputStream(f1).getChannel();
		dest.transferFrom(src, 0, fileLength);
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
