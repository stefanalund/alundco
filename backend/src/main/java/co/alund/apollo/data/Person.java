package co.alund.apollo.data;

import java.util.Calendar;

public class Person {
	
	public static final String MALE = "male";
	public static final String FEMALE = "female";

	private final String name;
	private final String gender;
	private final Calendar birthday;
	
	/**
	 * A person in the family.
	 * 
	 * @param name Full name of the person.
	 * @param gender Male or Female
	 * @param birthday Date of birth in form "20110527"
	 */
	public Person(String name, String gender, String birthday) {
		this.name = name;
		this.gender = gender;
		
		this.birthday = Calendar.getInstance();
		try {
			this.birthday.set(
					Integer.parseInt(birthday.substring(0, 3)),
					Integer.parseInt(birthday.substring(4, 5)),
					Integer.parseInt(birthday.substring(6, 7))
					);
		} catch (Exception e) {
			System.err.println("Failed to parse birthday string");
		}
		
		System.out.println(name + " celebrates their birthday at " + this.birthday);
	}

	public String getName() {
		return name;
	}

	public String getGender() {
		return gender;
	}

	public Calendar getBirthday() {
		return birthday;
	}
}
