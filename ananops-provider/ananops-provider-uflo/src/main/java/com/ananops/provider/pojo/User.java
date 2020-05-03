package com.ananops.provider.pojo;

import javax.persistence.*;


@Table(name = "t_user")
@Entity
public class User {

	@Id
    @GeneratedValue
	private Long id;
	
	@Column
	private String name;//姓名

	@Column
	private Integer age;//年龄

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}


}
