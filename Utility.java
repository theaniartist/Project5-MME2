/**
 * A Utility class for the parser.
 */

public class Utility 
{
	/**
	 * 
	 * @param str The string which will be used to count the number of characters.
	 * @param character The character to count the occurrences of.
	 * @return Return the number of occurrences.
	 */
	public static int numberOfOccurences(String str, char character)
	{
		int counter = 0;
		char [] charArray = str.toCharArray();
		for(int i = 0; i < charArray.length; i++)
		{
			if(charArray[i] == character)
			{
				counter++;
			}
		}
		return counter;
	}
}
