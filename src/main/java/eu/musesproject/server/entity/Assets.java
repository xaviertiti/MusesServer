package eu.musesproject.server.entity;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2015 Sweden Connectivity
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the assets database table.
 * 
 */
@Entity
@Table(name="assets")
@NamedQueries({
	@NamedQuery(name="Assets.findAll", 
			    query="SELECT a FROM Assets a"),
	@NamedQuery(name="Assets.findByLocation", 
	 			query="SELECT a FROM Assets a where a.location = :location"),
	@NamedQuery(name="Assets.findByTitle", 
				query="SELECT a FROM Assets a where a.title = :title"),
	@NamedQuery(name="Assets.findById", 
				query="SELECT a FROM Assets a where a.assetId = :assetId"),			
	@NamedQuery(name="Assets.deleteAssetByTitle", 
				query="delete FROM Assets a where a.title = :title")
})
public class Assets implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="asset_id", unique=true, nullable=false)
	private String assetId;

	@Column(name="confidential_level", nullable=false, length=1)
	private String confidentialLevel;

	@Column(length=100)
	private String description;

	@Column(nullable=false, length=100)
	private String location;

	@Column(nullable=false, length=30)
	private String title;

	@Column(nullable=false)
	private double value;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date available;

	//bi-directional many-to-one association to SimpleEvents
	@OneToMany(mappedBy="asset")
	private List<SimpleEvents> simpleEvents;

	//bi-directional many-to-one association to ThreatClue
	@OneToMany(mappedBy="asset")
	private List<ThreatClue> threatClues;

	public Assets() {
	}

	public String getAssetId() {
		return this.assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getConfidentialLevel() {
		return this.confidentialLevel;
	}

	public void setConfidentialLevel(String confidentialLevel) {
		this.confidentialLevel = confidentialLevel;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public Date getAvailable() {
		return this.available;
	}

	public void setAvailable(Date available) {
		this.available = available;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public List<SimpleEvents> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvents> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvents addSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setAsset(this);

		return simpleEvent;
	}

	public SimpleEvents removeSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setAsset(null);

		return simpleEvent;
	}

	public List<ThreatClue> getThreatClues() {
		return this.threatClues;
	}

	public void setThreatClues(List<ThreatClue> threatClues) {
		this.threatClues = threatClues;
	}

	public ThreatClue addThreatClue(ThreatClue threatClue) {
		getThreatClues().add(threatClue);
		threatClue.setAsset(this);

		return threatClue;
	}

	public ThreatClue removeThreatClue(ThreatClue threatClue) {
		getThreatClues().remove(threatClue);
		threatClue.setAsset(null);

		return threatClue;
	}


}