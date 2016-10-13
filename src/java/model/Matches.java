/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author asafgolan
 */
@Entity
@Table(name = "matches")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Matches.findAll", query = "SELECT m FROM Matches m"),
    @NamedQuery(name = "Matches.findById", query = "SELECT m FROM Matches m WHERE m.id = :id"),
    @NamedQuery(name = "Matches.findByType", query = "SELECT m FROM Matches m WHERE m.type = :type"),
    @NamedQuery(name = "Matches.findByDiscipline", query = "SELECT m FROM Matches m WHERE m.discipline = :discipline"),
    @NamedQuery(name = "Matches.findByStatus", query = "SELECT m FROM Matches m WHERE m.status = :status"),
    @NamedQuery(name = "Matches.findByTournamentId", query = "SELECT m FROM Matches m WHERE m.tournamentId = :tournamentId"),
    @NamedQuery(name = "Matches.findByNumber", query = "SELECT m FROM Matches m WHERE m.number = :number"),
    @NamedQuery(name = "Matches.findByStageNumber", query = "SELECT m FROM Matches m WHERE m.stageNumber = :stageNumber"),
    @NamedQuery(name = "Matches.findByGroupNumber", query = "SELECT m FROM Matches m WHERE m.groupNumber = :groupNumber"),
    @NamedQuery(name = "Matches.findByRoundNumber", query = "SELECT m FROM Matches m WHERE m.roundNumber = :roundNumber"),
    @NamedQuery(name = "Matches.findByDate", query = "SELECT m FROM Matches m WHERE m.date = :date"),
    @NamedQuery(name = "Matches.findByTimezone", query = "SELECT m FROM Matches m WHERE m.timezone = :timezone"),
    @NamedQuery(name = "Matches.findByMatchFormat", query = "SELECT m FROM Matches m WHERE m.matchFormat = :matchFormat")})
public class Matches implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "id")
    private String id;
    @Size(max = 30)
    @Column(name = "type")
    private String type;
    @Size(max = 30)
    @Column(name = "discipline")
    private String discipline;
    @Size(max = 30)
    @Column(name = "status")
    private String status;
    @Size(max = 30)
    @Column(name = "tournament_id")
    private String tournamentId;
    @Column(name = "number")
    private Integer number;
    @Column(name = "stage_number")
    private Integer stageNumber;
    @Column(name = "group_number")
    private Integer groupNumber;
    @Column(name = "round_number")
    private Integer roundNumber;
    @Column(name = "date")
    @Basic(optional=true)
    @Size(max = 45)
    //@Temporal(TemporalType.TIMESTAMP)
    private String date;
    @Size(max = 45)
    @Column(name = "timezone")
    private String timezone;
    @Size(max = 45)
    @Column(name = "match_format")
    private String matchFormat;
    @Lob
    @Size(max = 65535)
    @Column(name = "opponents")
    private String opponents;

    public Matches() {
    }

    public Matches(String id, String type, String discipline, String status, String tournamentId, Integer number, Integer stageNumber, Integer groupNumber, Integer roundNumber, String date, String timezone, String matchFormat, String opponents) {
        this.id = id;
        this.type = type;
        this.discipline = discipline;
        this.status = status;
        this.tournamentId = tournamentId;
        this.number = number;
        this.stageNumber = stageNumber;
        this.groupNumber = groupNumber;
        this.roundNumber = roundNumber;
        this.date = date;
        this.timezone = timezone;
        this.matchFormat = matchFormat;
        this.opponents = opponents;
    }

    public Matches(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(Integer stageNumber) {
        this.stageNumber = stageNumber;
    }

    public Integer getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(Integer groupNumber) {
        this.groupNumber = groupNumber;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getMatchFormat() {
        return matchFormat;
    }

    public void setMatchFormat(String matchFormat) {
        this.matchFormat = matchFormat;
    }

    public String getOpponents() {
        return opponents;
    }

    public void setOpponents(String opponents) {
        this.opponents = opponents;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Matches)) {
            return false;
        }
        Matches other = (Matches) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Matches[ id=" + id + " ]";
    }
    
}
