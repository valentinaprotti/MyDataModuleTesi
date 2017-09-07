package com.example.android.mydata_prova.controller;

import com.example.android.mydata_prova.model.services.IService;

import java.util.Date;
import java.util.List;

/**
 * Created by Valentina on 31/08/2017.
 */

public interface IController {
	void createMyDataUser(String firstName, String lastName, Date dateOfBirth, String emailAddress,
						  char[] password);

	void logInUser(String email, char[] password);

	void toggleStatus(IService selectedService, boolean status);

	String getAllSConsents(IService selectedService);

	void addService(IService service);

	void withdrawConsentForService(IService selectedService);

	List<IService> getAllActiveServicesForUser();

	boolean getADStatusForService(IService selectedService);

	void addNewServiceConsent(IService selectedService);

	String getAllDConsents(IService selectedService);
}
