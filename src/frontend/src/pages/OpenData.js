import React, { useState } from "react";
import Navbar from "../components/Navbar";
import "./OpenData.css";
import opendataApi from "../api/opendataApi"; 
import { useNavigate } from "react-router-dom";

const endpoints = [
  { key: "users", title: "All Users", fetch: opendataApi.getAllUsers },
  { key: "usersByPlace", title: "Users by Place", fetch: opendataApi.getUsersByPlace },
  { key: "usersByBirthday", title: "Users by Birthday", fetch: opendataApi.getUsersByBirthday },
  { key: "usersByGender", title: "Users by Gender", fetch: opendataApi.getUsersByGender },
  { key: "usersByRole", title: "Users and their Roles", fetch: opendataApi.getUsersByRole },
  { key: "usersByGenderRole", title: "Users by Gender & Role", fetch: opendataApi.getUsersByGenderRole },
  { key: "usersByGenderPlace", title: "Users by Gender & Place", fetch: opendataApi.getUsersByGenderPlace },
  { key: "usersByGenderBirthday", title: "Users by Gender & Birthday", fetch: opendataApi.getUsersByGenderBirthday },
  { key: "usersByBirthdayRole", title: "Users by Birthday & Role", fetch: opendataApi.getUsersByBirthdayRole },
  { key: "usersByBirthdayPlace", title: "Users by Birthday & Place", fetch: opendataApi.getUsersByBirthdayPlace },
  { key: "usersByPlaceRole", title: "Users by Place & Role", fetch: opendataApi.getUsersByPlaceRole },
  { key: "usersByGenderBirthdayRole", title: "Users by Gender, Birthday & Role", fetch: opendataApi.getUsersByGenderBirthdayRole },
  { key: "usersByGenderBirthdayPlace", title: "Users by Gender, Birthday & Place", fetch: opendataApi.getUsersByGenderBirthdayPlace },
  { key: "usersByGenderRolePlace", title: "Users by Gender, Role & Place", fetch: opendataApi.getUsersByGenderRolePlace },
  { key: "usersByBirthdayRolePlace", title: "Users by Birthday, Role & Place", fetch: opendataApi.getUsersByBirthdayRolePlace },
  { key: "documents", title: "All Documents", fetch: opendataApi.getAllDocuments },
];


const OpenData = () => {
  const [filter, setFilter] = useState("all");
  const navigate = useNavigate();

  const filteredEndpoints =
    filter === "all" ? endpoints : endpoints.filter((ep) => ep.key === filter);

  const handleView = async (fetchFn, title) => {
    try {
      const response = await fetchFn();
      console.log(`Data for ${title}:`, response.data);
      alert(`Check console for ${title} data.`);
    } catch (error) {
      console.error(`Error fetching ${title}:`, error);
      alert(`Failed to fetch ${title}`);
    }
  };

  return (
    <div className="home-container">
      <Navbar />
      <div className="home-content">
        <div className="welcome-section">
          <h1>Otvoreni podaci</h1>
          <p>Ovde možete pregledati sve naše otvorene podatke</p>
        </div>

        <div className="gradjanin-section">
          <div className="zahtevi-section">
            <div className="filter-container">
              <label>Show: </label>
              <select value={filter} onChange={(e) => setFilter(e.target.value)}>
                <option value="all">All</option>
                {endpoints.map((ep) => (
                  <option key={ep.key} value={ep.key}>
                    {ep.title}
                  </option>
                ))}
              </select>
            </div>

            <div className="zahtevi-list">
              {filteredEndpoints.map((ep) => (
                <div className="zahtev-item" key={ep.key}>
                  <div className="zahtev-header">
                    <h3>{ep.title}</h3>
                  </div>
                  <div className="zahtev-details">
                    <button onClick={() => navigate(`/opendata/${ep.key}`)}>Pregled</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OpenData;
