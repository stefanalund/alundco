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
	 * @param birthday Date of birth in form "2011-05-27"
	 */
	public Person(String name, String gender, String birthday) {
		this.name = name;
		this.gender = gender;
		
		this.birthday = Calendar.getInstance();
		try {
			String[] dateComponents = birthday.split("-"); 
			this.birthday.set(
					Integer.parseInt(dateComponents[0]),
					Integer.parseInt(dateComponents[1]),
					Integer.parseInt(dateComponents[2])
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
