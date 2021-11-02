package com.zenika.controller.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.zenika.controller.ContentServerDto;
import com.zenika.controller.Employee;
import com.zenika.controller.Person;

@Component
public class ContentServicePropertySourceProvider {

	private static Log logger = LogFactory.getLog(ContentServicePropertySourceProvider.class);

	private RestTemplate restTemplate;

	private RestTemplate getSecureRestTemplate() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout((60 * 1000 * 3) + 5000); // TODO 3m5s, make configurable?
		RestTemplate template = new RestTemplate(requestFactory);

		return template;
	}

	public ContentServerDto load(List<String> apps) {

		ContentServerDto dto = new ContentServerDto();

		RestTemplate restTemplate = this.restTemplate == null ? getSecureRestTemplate() : this.restTemplate;

		try {

			// Try all the labels until one works
			for (String app : apps) {
				EnvironmentDto result = getRemoteEnvironment(restTemplate, app);
				if (result != null) {
					logger.info(String.format(
							"Located environment: name=%s, profiles=%s, label=%s, version=%s, state=%s",
							result.getName(), result.getProfiles() == null ? "" : Arrays.asList(result.getProfiles()),
							result.getLabel(), result.getVersion(), result.getState()));

					if (result.getPropertySources() != null) { // result.getPropertySources() can be null if using xml
						for (PropertySourceDto source : result.getPropertySources()) {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = (Map<String, Object>) source.getSource();
							System.out.print("Value " + map);

							if (app.contains("app1")) {
								List<Person> list = getPersons(map);
								dto.setNames(list);

								for (Person p : list) {
									System.out.print("Person  :\t \t" + p);
								}

							} else if (app.contains("app2")) {
								List<Employee> list = getEmployee(map);
								dto.setEmployee(list);
							}

						}
					}

				}
			}
		} catch (HttpServerErrorException e) {

		} catch (Exception e) {
		}

		return dto;

	}

	private List<Person> getPersons(Map<String, Object> map) {
		List<Person> personList = new ArrayList<>();

		String previousKey = null;

		for (String key : map.keySet()) {

			if (previousKey == null || !key.contains(previousKey)) {
				Person person = new Person();
				previousKey = "lob.names[" + indexValue(key) + "]";

				String name = (String) map.get(previousKey + ".name");
				Integer age = (Integer) map.get(previousKey + ".age");
				Integer salary = (Integer) map.get(previousKey + ".salary");

				person.setName(name);
				person.setAge(age);
				person.setSalary(salary);

				if (person != null) {
					personList.add(person);
				}

			}

		}

		return personList;
	}

	private List<Employee> getEmployee(Map<String, Object> map) {
		List<Employee> personList = new ArrayList<>();

		String previousKey = null;

		for (String key : map.keySet()) {

			if (previousKey == null || !key.contains(previousKey)) {
				Employee person = new Employee();
				previousKey = "lob.employee[" + indexValue(key) + "]";

				String name = (String) map.get(previousKey + ".name");
				Integer age = (Integer) map.get(previousKey + ".age");
				Integer salary = (Integer) map.get(previousKey + ".salary");

				person.setName(name);
				person.setAge(age);
				person.setSalary(salary);

				if (person != null) {
					personList.add(person);
				}

			}

		}

		return personList;
	}

	public static String indexValue(String value) {

		int startIndex = value.indexOf("[");
		int endIndex = value.indexOf("]");

		return value.substring(startIndex + 1, endIndex);

	}

	private EnvironmentDto getRemoteEnvironment(RestTemplate restTemplate, String app) {

		ResponseEntity<EnvironmentDto> response = null;

		try {
			HttpHeaders headers = new HttpHeaders();
			final HttpEntity<Void> entity = new HttpEntity<>((Void) null, headers);
			response = restTemplate.exchange(app, HttpMethod.GET, entity, EnvironmentDto.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
				throw e;
			}
		}

		if (response == null || response.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		EnvironmentDto result = response.getBody();
		return result;
	}

}
