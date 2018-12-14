/**
 * A Utility class for the parser.
 */

public class Utility 
{
	/**
	 * Method checks if the string that is passed has any of the specificed characters that was also passed. If
	 * there is an occurence, the counter would increment.
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
