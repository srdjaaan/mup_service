import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import opendataApi from "../api/opendataApi";
import "./OpenDataView.css";
import { Pie } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { v4 as uuidv4 } from "uuid";

ChartJS.register(ArcElement, Tooltip, Legend);

const endpointsMap = {
  users: { title: "Svi podaci o korisnicima", fetch: opendataApi.getAllUsers, excel: opendataApi.exportUsersExcel },
  usersByPlace: { title: "Korisnici i njihova rodna mesta", fetch: opendataApi.getUsersByPlace, excel: opendataApi.exportUsersByPlaceExcel },
  usersByBirthday: { title: "Korisnici i njihovi datumi rodjenja", fetch: opendataApi.getUsersByBirthday, excel: opendataApi.exportUsersByBirthdayExcel },
  usersByGender: { title: "Korisnici i njihov pol", fetch: opendataApi.getUsersByGender, excel: opendataApi.exportUsersByGenderExcel },
  usersByRole: { title: "Korisnici i njihova uloga", fetch: opendataApi.getUsersByRole, excel: opendataApi.exportUsersByRoleExcel },
  usersByGenderRole: { title: "Korisnici i njihov pol i uloga", fetch: opendataApi.getUsersByGenderRole, excel: opendataApi.exportUsersByGenderRoleExcel },
  usersByGenderPlace: { title: "Korisnici i njihov pol i mesto rodjenja", fetch: opendataApi.getUsersByGenderPlace, excel: opendataApi.exportUsersByGenderPlaceExcel },
  usersByGenderBirthday: { title: "Korisnici i njihov pol i datum rodjenja", fetch: opendataApi.getUsersByGenderBirthday, excel: opendataApi.exportUsersByGenderBirthdayExcel },
  usersByBirthdayRole: { title: "Korisnici i njihova uloga i datum rodjenja", fetch: opendataApi.getUsersByBirthdayRole, excel: opendataApi.exportUsersByBirthdayRoleExcel },
  usersByBirthdayPlace: { title: "Korisnici i njihov datum i mesto rodjenja", fetch: opendataApi.getUsersByBirthdayPlace, excel: opendataApi.exportUsersByBirthdayPlaceExcel },
  usersByPlaceRole: { title: "Korisnici i njihova uloga i mesto rodjenja", fetch: opendataApi.getUsersByPlaceRole, excel: opendataApi.exportUsersByPlaceRoleExcel },
  usersByGenderBirthdayRole: { title: "Korisnici i njihov pol, datum rodjenja i uloga", fetch: opendataApi.getUsersByGenderBirthdayRole, excel: opendataApi.exportUsersByGenderBirthdayRoleExcel },
  usersByGenderBirthdayPlace: { title: "Korisnici i njihov pol, datum i mesto rodjenja", fetch: opendataApi.getUsersByGenderBirthdayPlace, excel: opendataApi.exportUsersByGenderBirthdayPlaceExcel },
  usersByGenderRolePlace: { title: "Korisnici i njihov pol, uloga i mesto rodjenja", fetch: opendataApi.getUsersByGenderRolePlace, excel: opendataApi.exportUsersByGenderRolePlaceExcel },
  usersByBirthdayRolePlace: { title: "Korisnici i njihovo mesto i datum rodjenja kao i njihova uloga", fetch: opendataApi.getUsersByBirthdayRolePlace, excel: opendataApi.exportUsersByBirthdayRolePlaceExcel },
  documents: { title: "Svi dokumenti", fetch: opendataApi.getAllDocuments, excel: opendataApi.exportAllDocuments }
};

const COLORS = ["#36A2EB", "#FF6384", "#FFCE56", "#9CCC65", "#FF7043", "#A569BD", "#F39C12"];

const OpenDataView = () => {
  const { endpointKey } = useParams();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [shareLink, setShareLink] = useState("");

  const endpoint = endpointsMap[endpointKey];

  useEffect(() => {
    if (!endpoint) return;

    setLoading(true);
    endpoint.fetch()
      .then(res => setData(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [endpointKey]);

  const handleDownload = () => {
    if (!endpoint.excel) return;

    endpoint.excel()
      .then(res => {
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", `${endpointKey}.xlsx`);
        document.body.appendChild(link);
        link.click();
        link.remove();
      })
      .catch(err => console.error(err));
  };

  const handleGenerateShareLink = () => {
    const token = uuidv4();
    const expiry = new Date(Date.now() + 1000 * 60 * 60);
    const url = `${window.location.origin}/opendataview/${endpointKey}?shareToken=${token}`;
    setShareLink(url);
    alert(`Temporary share link generated!\nLink expires at: ${expiry.toLocaleString()}`);
  };

  const handleCopyLink = () => {
    if (!shareLink) {
      alert("Please generate a share link first.");
      return;
    }

    navigator.clipboard.writeText(shareLink)
      .then(() => alert("Link copied to clipboard!"))
      .catch(err => console.error("Failed to copy:", err));
  };

  const handleShareEmail = () => {
    if (!shareLink) {
      alert("Please generate a share link first.");
      return;
    }

    const subject = encodeURIComponent("Shared Open Data Link");
    const body = encodeURIComponent(`Hi,\n\nYou can view the open data here: ${shareLink}\n\nBest regards.`);
    window.location.href = `mailto:?subject=${subject}&body=${body}`;
  };

  const renderPieCharts = () => {
    if (data.length === 0) return null;

    const firstRow = data[0];
    const categoricalFields = Object.keys(firstRow).filter(key => {
      if (["name", "lastname", "createdAt", "expiresAt", "createdat", "expiresat"].includes(key.toLowerCase())) return false;
      return typeof firstRow[key] === "string" || typeof firstRow[key] === "boolean" || key.toLowerCase().includes("birthday");
    });

    if (categoricalFields.length === 0) return null;

    return categoricalFields.map((field, idx) => {
      const counts = data.reduce((acc, row) => {
        let value = row[field];
        if (!value) value = "Unknown";

        if (field.toLowerCase().includes("birthday") && value) {
          const dateObj = new Date(value);
          if (!isNaN(dateObj)) value = dateObj.getFullYear();
        }

        acc[value] = (acc[value] || 0) + 1;
        return acc;
      }, {});

      const chartData = {
        labels: Object.keys(counts),
        datasets: [
          {
            data: Object.values(counts),
            backgroundColor: Object.keys(counts).map((_, i) => COLORS[i % COLORS.length])
          }
        ]
      };

      return (
        <div key={idx} style={{ maxWidth: 500, margin: "40px auto" }}>
          <h3 style={{ textAlign: "center" }}>{`Distribution by ${field}`}</h3>
          <Pie data={chartData} />
        </div>
      );
    });
  };

  if (!endpoint) return <div>Netacan endpoint</div>;

  return (
    <div className="home-container">
      <Navbar />
      <div className="home-content">
        <div className="welcome-section">
          <h1>{endpoint.title}</h1>
        </div>

        <div className="gradjanin-section">
          <div className="zahtevi-section">
            {loading ? (
              <div className="loading-container">
                <div className="loading-spinner"></div>
                <p>Loading data...</p>
              </div>
            ) : data.length === 0 ? (
              <div className="no-zahtevi">Nema podataka.</div>
            ) : (
              <>
                <table className="open-data-table">
                  <thead>
                    <tr>
                      {Object.keys(data[0]).map((key) => (
                        <th key={key}>{key}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {data.map((row, idx) => (
                      <tr key={idx}>
                        {Object.values(row).map((val, i) => {
                          if (val && typeof val === "object") return <td key={i}>{JSON.stringify(val)}</td>;
                          return <td key={i}>{val?.toString()}</td>;
                        })}
                      </tr>
                    ))}
                  </tbody>
                </table>
                <div style={{ marginTop: 20, display: "flex", gap: "20px" }}>
                  {endpoint.excel && (
                    <button className="download-btn" onClick={handleDownload}>
                      Download Data
                    </button>
                  )}
                  <button className="share-btn" onClick={handleGenerateShareLink}>
                      Generate Share Link
                    </button>
                    <button className="share-btn" onClick={handleCopyLink}>
                      Copy Link
                    </button>
                    <button className="share-btn" onClick={handleShareEmail}>
                      Share via Email
                    </button>
                </div>
                {renderPieCharts()}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default OpenDataView;
