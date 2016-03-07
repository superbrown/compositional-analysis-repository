package gov.energy.nrel.dataRepositoryApp.utilities;


import java.util.Comparator;

public class AlphanumericComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {

		String string_1 = (String) o1;
		String string_2 = (String) o2;

		Double number_1 = null;
		try {
			number_1 = Double.parseDouble(string_1);
		} catch (NumberFormatException e1) {
		}

		Double number_2 = null;
		try {
			number_2 = Double.parseDouble(string_2);
		} catch (NumberFormatException e) {
		}

		if (number_1 != null) {

			if (number_2 != null) {
				// both are numbers
				return number_1.compareTo(number_2);
			}
			else {
				// first is a number, second is not
				return 1;
			}
		}
		else {

			if (number_2 != null) {
				// first is not a number, second is
				return -1;
			}
			else {
				// neither are numbers
				return string_1.compareTo(string_2);
			}
		}
	}
}
