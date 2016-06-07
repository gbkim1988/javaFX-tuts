package kr.greedyeater.address.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import kr.greedyeater.util.LocalDateAdapter;

import javax.xml.bind.annotation.XmlElement;

import java.time.LocalDate;
import java.util.List;

@XmlRootElement(name ="persons") // Defines the name of the root element.
public class PersonListWrapper {
	private List<Person> persons;
	
	@XmlElement(name = "person") // is an optional name we can specify for the element
	public List<Person> getPersons(){
		return persons;
	}
	
	public void setPersons(List<Person> persons){
		this.persons = persons;
	}
}
