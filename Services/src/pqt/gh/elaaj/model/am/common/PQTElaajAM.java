package pqt.gh.elaaj.model.am.common;

import oracle.jbo.ApplicationModule;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Fri Aug 20 20:00:58 PKT 2021
// ---------------------------------------------------------------------
public interface PQTElaajAM extends ApplicationModule {
    String VerifyUserAuth(String pUserName, String pUserPassword);

    void executeBenefitDetail();

    void Apply();
}

