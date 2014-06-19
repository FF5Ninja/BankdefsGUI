package cnuphys.bCNU.treegui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
@author Amanda Lee, Brandon Rusk
*/

public class Readbankdef {
	// increments an int while the array is null. allows us to read in from
	// files that have
	// more than one tag and set of bank/nums
	private static XMLInputFactory f = null;
	private static XMLStreamReader rdr;

	public static int startHere(String[][] array, int i) {
		int x = 0;
		while (array[i][x] != null) {
			x++;
		}
		return x;
	}

	// given an integer, find the tags that belong to the array at that index.
	// the second element
	// is the array position of the tag that allows us to check that it is not
	// literally the same
	// tag.
	public static ArrayList<String> findTags(String[][] filenames, int x) {
		ArrayList<String> ans = new ArrayList<String>();
		if (filenames[x][1] != null) {
			ans.add(filenames[x][1]);
			ans.add(x + ",1");
		}
		int i = 0;
		while (filenames[x][i] != null) {
			if (filenames[x][i].equals("second")) {
				ans.add(filenames[x][i + 1]);
				int b = i + 1;
				ans.add(x + "," + b);
			}
			i++;
		}
		return ans;
	}

	// this one returns true if the elements in the array match.
	public static ArrayList<String> existsIn(String[][] filenames,
			ArrayList<String> tagandnums) {
		String tag = tagandnums.get(1);
		String index = tagandnums.get(0);
		ArrayList<String> nums = new ArrayList<String>();
		ArrayList<String> hold = new ArrayList<String>();
		boolean tagtrue = false;
		int x = 0;
		if (tagandnums.size() > 1) {
			for (int k = 2; k < tagandnums.size(); k++) {
				String num = tagandnums.get(k);
				for (int i = 0; i < filenames.length; i++) {
					x = 0;
					hold = findTags(filenames, i);
					if (!hold.isEmpty()) {

						if (hold.get(x).equals(tag)
								&& !hold.get(x + 1).equals(index)) {
							tagtrue = true;
						}
					} else {
						tagtrue = false;
					}
					int j = 2;
					while (filenames[i][j] != null) {
						if (filenames[i][j].equals("second")) {// increment x
																// only when we
																// hit a
																// "second"
							x += 2;
							if (hold.get(x).equals(tag)
									&& !hold.get(x + 1).equals(index)) {
								tagtrue = true;
							} else {
								tagtrue = false;
							}

						}
						if (tagtrue) {
							if (filenames[i][j] != null) {
								if (filenames[i][j].equals(num)) {
									nums.add(num);
								}
							}
						}
						j++;
					}
					tagtrue = false;
				}

			}
		}
		nums.add("stop");
		return nums;
	}

	public static void main(String[] args) {
		// get the file separator of the system you're using
		String sep = System.getProperty("file.separator");
		if (f == null) {
			f = XMLInputFactory.newInstance();

		}

		try {
			File folder = new File(args[0]);
			File[] listOfFiles = folder.listFiles();

			// Below is our array. I use this array for everything. The 0th
			// element is the name of
			// the file. the 1st element is bank/tag, and then nums. If there's
			// more than one set of
			// bank and nums, then there'll be the world "second" between the
			// sets.
			if (listOfFiles.length < 1) {

				return;
			}
			String[][] filenames = new String[listOfFiles.length][500];
			int i = 0;
			for (File file : listOfFiles) {
				if (file.isFile()) {
					filenames[i][0] = file.getName();
					i++;
				}
			}
			// Generates the XMLFactory we use to read from the file.
			if (f == null) {
				f = XMLInputFactory.newInstance();
			}
			int j = 2;
			try {
				// Main loop. steps through XML file and pulls out info we need.
				for (i = 1; i < filenames.length; i++) {
					rdr = f.createXMLStreamReader(new FileReader(args[0] + sep
							+ filenames[i][0]));
					j = 2;
					while (rdr.hasNext()) {
						if (rdr.next() == XMLStreamConstants.START_ELEMENT) {
							if (rdr.getLocalName().equals("bank")) {
								// adds first 'tag' String to array
								if (filenames[i][1] == null) {
									filenames[i][1] = rdr.getAttributeValue("",
											"tag");
								} else {
									// increments counter, then inserts a marker
									// ("second") before
									// adding a subsequent "tag" String to the
									// array.
									int x = startHere(filenames, i);
									filenames[i][x] = "second";
									filenames[i][x + 1] = rdr
											.getAttributeValue("", "tag");
									j += 2;
								}
							}
							if (rdr.getLocalName().equals("column")) {
								filenames[i][j] = rdr.getAttributeValue("",
										"num");
								j++;
							}
						}
					}
				}
				ArrayList<String> holdAns = new ArrayList<String>();
				for (int k = 0; k < filenames.length; k++) {
					// for (int l=0; l<filenames[0].length; l++){
					int l = 1;
					ArrayList<String> tagandnums = new ArrayList<String>();
					ArrayList<String> nums = new ArrayList<String>();
					while (filenames[k][l] != null) {

						if (!filenames[k][l].equals("second")) {
							if (tagandnums.isEmpty()) {
								String index = k + "," + l;
								tagandnums.add(index);
							}
							tagandnums.add(filenames[k][l]);
						}
						// }
						if (filenames[k][1] != null
								&& filenames[k][l].equals("second")) {
							nums = existsIn(filenames, tagandnums);
							String holdnum = nums.get(0);
							int p = 1;
							if (!holdnum.equals("stop")) {
								// checks to see if
								// we've returned a
								// match; holdnum
								// will only equal
								// stop if there is
								// no match.
								while (!nums.get(p).equals("stop")) {
									holdnum += ", " + nums.get(p);
									p++;
								}
								holdAns.add(filenames[k][0]);
								holdAns.add(tagandnums.get(1));
								holdAns.add(holdnum);

							}
							while (!tagandnums.isEmpty()) {
								tagandnums.remove(0);
							}
						}
						l++;
					}
					if (filenames[k][1] != null) {
						nums = existsIn(filenames, tagandnums);
						String holdnum = nums.get(0);
						int p = 1;
						if (!holdnum.equals("stop")) {
							while (!nums.get(p).equals("stop")) {
								holdnum += ", " + nums.get(p);
								p++;
							}
							holdAns.add(filenames[k][0]);
							holdAns.add(tagandnums.get(1));
							holdAns.add(holdnum);
						}
					}

				}

				String hold = "";
				String print = "x";
				ArrayList<String> last = new ArrayList<String>();
				for (int m = 0; m < holdAns.size(); m++) {
					print = holdAns.get(0) + " and ";
					holdAns.remove(0);
					String holdtag = holdAns.get(0);
					holdAns.remove(0);
					String holdnums = holdAns.get(0);
					holdAns.remove(0);
					for (int n = 0; n < holdAns.size(); n++) {
						if (holdtag.equals(holdAns.get(n))
								&& holdnums.equals(holdAns.get(n + 1))) {
							print += holdAns.get(n - 1)
									+ " have duplicate tag " + holdtag
									+ " and duplicate nums " + holdnums + ".";
							hold += print + "\n";

						}
					}

				}

				int response = JOptionPane.showConfirmDialog(null, hold,
						"Error", JOptionPane.PLAIN_MESSAGE,
						JOptionPane.OK_OPTION);

				if (response == JOptionPane.OK_OPTION) {
					SAXJTreeStart.main(args);
				}

				if (print.equals("x")) {
					last.add("There were no duplicates.");
				}

			} catch (FileNotFoundException | XMLStreamException e) {
				String error = "There is a syntax error in the file " + folder
						+ "\\" + filenames[i][0] + "\n\n"
						+ e.getLocalizedMessage();
				JOptionPane.showConfirmDialog(null, error, "Error",
						JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION);

			}

		} catch (Exception e) {
			String error = "Could not find the location specified.";
			JOptionPane.showConfirmDialog(null, error, "Error",
					JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION);
			SAXJTreeStart.main(args);
		}

	}

}