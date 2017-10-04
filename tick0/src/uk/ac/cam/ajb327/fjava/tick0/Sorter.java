package uk.ac.cam.ajb327.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sorter {

	private String f2;
	private String f1;
	private int fileLength;
	private int initialSortInts;

	Sorter(String F1, String F2, int FileLength, int InitialSortInts) {
		f1 = F1;
		f2 = F2;
		fileLength = FileLength;
		initialSortInts = InitialSortInts;
	}

	public void sort() throws IOException {
		initialSort();
		mergeSort();
	}

	private DataOutputStream getOutputStream(String location) throws IOException {
		return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(location)));
	}

	private DataInputStream getInputStream(String location) throws IOException {
		return new DataInputStream(new BufferedInputStream(new FileInputStream(location)));
	}

	private void initialSort() throws IOException {

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

	}

	private void mergeSort() throws IOException {

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

		copyToCorrectFile(readingFromF1);

	}

	private void copyToCorrectFile(boolean fileInF1) throws IOException {
		if (!fileInF1) {
			FileChannel src = new FileInputStream(f2).getChannel();
			FileChannel dest = new FileOutputStream(f1).getChannel();
			dest.transferFrom(src, 0, fileLength);
		}
	}

}
