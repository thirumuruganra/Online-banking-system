package com.obs.bean;

public class CustomerBean {
	private int cACno;
	private String cname;
	private double cbal;
	private String cmail;
	private String cpass;
	private String cmob;
	private String cadd;
	private String schoolName;
	
	public CustomerBean() {
		super();
	}

    public CustomerBean(int cACno, String cname, double cbal, String cmail, String cpass, String cmob, String cadd, String schoolName) {
        super();
        this.cACno = cACno;
        this.cname = cname;
        this.cbal = cbal;
        this.cmail = cmail;
        this.cpass = cpass;
        this.cmob = cmob;
        this.cadd = cadd;
        this.schoolName = schoolName;
    }

	public int getcACno() {
		return cACno;
	}

	public void setcACno(int cACno) {
		this.cACno = cACno;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public double getCbal() {
		return cbal;
	}

	public void setCbal(double cbal) {
		this.cbal = cbal;
	}

	public String getCmail() {
		return cmail;
	}

	public void setCmail(String cmail) {
		this.cmail = cmail;
	}

	public String getCpass() {
		return cpass;
	}

	public void setCpass(String cpass) {
		this.cpass = cpass;
	}

	public String getCmob() {
		return cmob;
	}

	public void setCmob(String cmob) {
		this.cmob = cmob;
	}

	public String getCadd() {
		return cadd;
	}

	public void setCadd(String cadd) {
		this.cadd = cadd;
	}

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Override
    public String toString() {
        return "CustomerBean [cACno=" + cACno + ", cname=" + cname + ", cbal=" + cbal + ", cmail=" + cmail + ", cpass="
                + cpass + ", cmob=" + cmob + ", cadd=" + cadd + ", schoolName=" + schoolName + "]";
    }
}