package com.example.vaibhav.irisplacements;

import android.os.Parcel;
import android.os.Parcelable;


public class Entry implements Parcelable{
    private int id;
    private String recruitment_date;
    private String deadline;
    private String recruitment_type;
    private Company company;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecruitment_date() {
        return recruitment_date;
    }

    public void setRecruitment_date(String recruitment_date) {
        this.recruitment_date = recruitment_date;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getRecruitment_type() {
        return recruitment_type;
    }

    public void setRecruitment_type(String recruitment_type) {
        this.recruitment_type = recruitment_type;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(recruitment_date);
        dest.writeString(deadline);
        dest.writeString(recruitment_type);
        dest.writeParcelable(company,0);
        dest.writeString(url);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Entry>() {

        @Override
        public Entry createFromParcel(Parcel source) {
            Entry entry = new Entry();
            entry.setId(source.readInt());
            entry.setRecruitment_date(source.readString());
            entry.setDeadline(source.readString());
            entry.setRecruitment_type(source.readString());
            Company company = source.readParcelable(Company.class.getClassLoader());
            entry.setCompany(company);
            entry.setUrl(source.readString());
            return entry;
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[0];
        }
    };
}
class Company implements Parcelable{
    private int id;
    private String name;
    private String company_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany_type() {
        return company_type;
    }

    public void setCompany_type(String company_type) {
        this.company_type = company_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(company_type);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Company>() {

        @Override
        public Company createFromParcel(Parcel source) {
            Company company = new Company();
            company.setId(source.readInt());
            company.setName(source.readString());
            company.setCompany_type(source.readString());
            return company;
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[0];
        }
    };
}