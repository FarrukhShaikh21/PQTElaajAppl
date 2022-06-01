package pqt.gh.elaaj.view.request;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

import java.sql.SQLException;

import javax.el.ELContext;

import javax.el.MethodExpression;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichListView;

import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.adf.view.rich.component.rich.output.RichOutputFormatted;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;

import oracle.jbo.ViewObject;

import oracle.jbo.domain.BlobDomain;

import org.apache.myfaces.trinidad.model.UploadedFile;

public class BenefitDetailBean {
    private RichListView selectMaster;
    private RichInputFile rif;

    //    private String FileDownloadPath = "/u01/ADF_DMS/BTCS_CMS_FILE/DOWNLOAD/";
    //    private String FileUploadPath = "/u01/ADF_DMS/BTCS_CMS_FILE/UPLOAD/";

    private String FileDownloadPath = "/u01/ADF_DMS/URC_FILE/DOWNLOAD/";
    private String FileUploadPath = "/u01/ADF_DMS/URC_FILE/UPLOAD/";

    private RichOutputFormatted rif1;
    private RichInputFile rif2;
    //private String FileUploadPath = "C:\\hello\\";
    public BenefitDetailBean() {
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void executeMaster(org.apache.myfaces.trinidad.event.SelectionEvent selectionEvent) {
        ELContext elcontext = FacesContext.getCurrentInstance().getELContext();
        MethodExpression methodExpression =
            FacesContext.getCurrentInstance().getApplication().getExpressionFactory().createMethodExpression(elcontext,
                                                                                                             "#{bindings.MainMenuVO1.treeModel.makeCurrent}",
                                                                                                             Object.class,
                                                                                                             new Class[] { org.apache.myfaces.trinidad.event.SelectionEvent.class });
        methodExpression.invoke(elcontext, new Object[] { selectionEvent });
        //        BindingContainer bindings = getBindings();
        //        OperationBinding operationBinding = bindings.getOperationBinding("executeBenefitDetail");
        //        operationBinding.execute();
    }

    public void setSelectMaster(RichListView selectMaster) {
        this.selectMaster = selectMaster;
    }

    public RichListView getSelectMaster() {
        return selectMaster;
    }

    public void uploadFileEventClaimReimburse(ValueChangeEvent vc) {
        
    }

    public void uploadFileEventClaimIntimation(ValueChangeEvent vc) {
        if (vc.getNewValue() != null) {

            UploadedFile fileVal = (UploadedFile) vc.getNewValue();
            BindingContext bcx = BindingContext.getCurrent();
            BindingContainer bc = bcx.getCurrentBindingsEntry();
            DCBindingContainer dbc = (DCBindingContainer) bc;
            DCIteratorBinding iter = dbc.findIteratorBinding("ElaajClaimDmsDocEOVO1Iterator");

            String vLineIDPk = null;
            //            vtime = (String)System.currentTimeMillis();
            Row row = iter.getCurrentRow();
            try {
                vLineIDPk = row.getAttribute("DocIdPk").toString();
            } catch (NullPointerException ex) {
                vLineIDPk = null;
            }

            //row.setAttribute("FileLocation", filePath);
            //            row.setAttribute("LineIdPk", lineIdPk);
            try {
                vLineIDPk = row.getAttribute("DocIdPk").toString();
            } catch (NullPointerException ex) {
                vLineIDPk = null;
            }

            try {
                //Save image in Blob column in database
                row.setAttribute("DocFile", createBlobDomain(fileVal));
                String fileName = (String) fileVal.getFilename();

            } catch (Exception ex) {
                System.out.println("Exception-" + ex);
            }

            String fileName = (String) fileVal.getFilename();
            row.setAttribute("FileName", vLineIDPk + fileName);

            String fileType = (String) fileVal.getContentType();
            row.setAttribute("FileType", fileType);
            
            fileUploadAction(vLineIDPk + fileName, vLineIDPk);
        }
    }

    /**Method to create blobdomain for uploaded file
     * */
    private BlobDomain createBlobDomain(UploadedFile file) {
        InputStream in = null;
        BlobDomain blobDomain = null;
        OutputStream out = null;

        try {
            in = file.getInputStream();

            blobDomain = new BlobDomain();
            out = blobDomain.getBinaryOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead = 0;

            while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }

        return blobDomain;
    }

    private String uploadFile(UploadedFile file, String pIteratorBinding, String pClaimId) {


        BindingContext bcx = BindingContext.getCurrent();
        BindingContainer bc = bcx.getCurrentBindingsEntry();
        DCBindingContainer dbc = (DCBindingContainer) bc;
        DCIteratorBinding iter = dbc.findIteratorBinding("ElaajClaimIntimateEOVO1Iterator");

        String vLineIDPk = null;
        //            vtime = (String)System.currentTimeMillis();
        Row row = iter.getCurrentRow();
        try {
            vLineIDPk = row.getAttribute("ElaajClaimIntimateId").toString();
        } catch (NullPointerException ex) {
            vLineIDPk = null;
        }

        UploadedFile myfile = file;
        System.out.println("this file path");
        System.out.println(FileUploadPath + vLineIDPk + myfile.getFilename());
        if (myfile != null) {
            try {
                InputStream is = file.getInputStream();

                OutputStream os = new FileOutputStream(FileUploadPath + vLineIDPk + myfile.getFilename());
                int readData;
                while ((readData = is.read()) != -1) {
                    os.write(readData);
                }
                is.close();
                os.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return "Error while Uploading";
            }
        }
        return FileUploadPath + vLineIDPk + myfile.getFilename();
    }

    public String fileUploadAction(String pFileName, String VClaimNo) {
        String DmsDocumentReference = null, vFileName = null;
        BindingContainer bc = getBindings();
        OperationBinding ob = bc.getOperationBinding("UploadFile1");


        DCBindingContainer dbc = (DCBindingContainer) bc;
        DCIteratorBinding iter = dbc.findIteratorBinding("ElaajClaimIntimateEOVO1Iterator");

        ViewObject hvo = iter.getViewObject();

        String vLineIDPk = null;
        Row row = iter.getCurrentRow();
        try {
            DmsDocumentReference = hvo.getCurrentRow().getAttribute("DmsDocumentReference").toString();
        } catch (Exception e) {
            DmsDocumentReference = "Create";
        }

        try {
            vLineIDPk = row.getAttribute("ElaajClaimIntimateId").toString();
        } catch (NullPointerException ex) {
            vLineIDPk = null;
        }

        try {
            vFileName = row.getAttribute("AttachFile1").toString();
        } catch (Exception e) {
            vFileName = "zzz.jpg";
        }

        ob.getParamsMap().put("DocumentReference", DmsDocumentReference);
        ob.getParamsMap().put("SchemaName", "FT_Elaaj_Claims");
        ob.getParamsMap().put("RepoName", "FT_Elaaj_Claims");
        ob.getParamsMap().put("PrimaryKey", "Claim_Id," + VClaimNo);
        ob.getParamsMap().put("FileLocation", FileUploadPath);
        ob.getParamsMap().put("FileName", vFileName);
        ob.getParamsMap().put("Parameters", "Claim_Id," + VClaimNo);

        System.out.println("DocumentReference >>> " + DmsDocumentReference);
        System.out.println("PrimaryKey >>> " + VClaimNo);
        System.out.println("FileLocation >>> " + FileUploadPath);
        System.out.println("FileName >>> " + vFileName);
        System.out.println("Parameters >>> " + VClaimNo);

        Object result = ob.execute();

        if (!ob.getErrors().isEmpty()) {

            String vNull = null;

        } else {
            hvo.getCurrentRow().setAttribute("DmsDocumentReference", result);
            DmsDocumentReference = (String) result;
        }
        return null;
    }


    //    public void setRif(RichInputFile rif) {
    //        this.rif = rif;
    //    }
    //
    //    public RichInputFile getRif() {
    //        return rif;
    //    }

    public void setRif1(RichOutputFormatted rif1) {
        this.rif1 = rif1;
    }

    public RichOutputFormatted getRif1() {
        return rif1;
    }

    //    public void setRif2(RichInputFile rif2) {
    //        this.rif2 = rif2;
    //    }
    //
    //    public RichInputFile getRif2() {
    //        return rif2;
    //    }
    public void dmsDocUploadFileEvent(ValueChangeEvent vc) {
        // Add event code here...
        if (vc.getNewValue() != null) {
            UploadedFile fileVal = (UploadedFile) vc.getNewValue();
            BindingContext bcx = BindingContext.getCurrent();
            BindingContainer bc = bcx.getCurrentBindingsEntry();
            DCBindingContainer dbc = (DCBindingContainer) bc;
            DCIteratorBinding iter = dbc.findIteratorBinding("ElaajClaimDmsDocEOVO1Iterator");

            String vLineIDPk = null;
            //            vtime = (String)System.currentTimeMillis();
            Row row = iter.getCurrentRow();
            //            String lineIdPk = null;
            String filePath = uploadFile1(fileVal);
            row.setAttribute("FileLocation", filePath);
            //            row.setAttribute("LineIdPk", lineIdPk);
            try {
                vLineIDPk = row.getAttribute("DocIdPk").toString();
            } catch (NullPointerException ex) {
                vLineIDPk = null;
            }

            try {
                //Save image in Blob column in database
                row.setAttribute("DocFile", createBlobDomain(fileVal));
                String fileName = (String) fileVal.getFilename();

            } catch (Exception ex) {
                System.out.println("Exception-" + ex);
            }

            String fileName = (String) fileVal.getFilename();
            row.setAttribute("FileName", vLineIDPk + fileName);

            String fileType = (String) fileVal.getContentType();
            row.setAttribute("FileType", fileType);
        }
    }


    private String uploadFile1(UploadedFile file) {
        BindingContext bcx = BindingContext.getCurrent();
        BindingContainer bc = bcx.getCurrentBindingsEntry();
        DCBindingContainer dbc = (DCBindingContainer) bc;
        DCIteratorBinding iter = dbc.findIteratorBinding("ElaajClaimDmsDocEOVO1Iterator");

        String vLineIDPk = null;
        Row row = iter.getCurrentRow();
        //        row.setAttribute("LineIdPk", lineIdPk);

        try {
            vLineIDPk = row.getAttribute("DocIdPk").toString();
        } catch (NullPointerException ex) {
            vLineIDPk = null;
        }

        UploadedFile myfile = file;
        if (myfile != null) {
            try {
                InputStream is = file.getInputStream();
                OutputStream os = new FileOutputStream(FileUploadPath + vLineIDPk + myfile.getFilename());
                int readData;
                while ((readData = is.read()) != -1) {
                    os.write(readData);
                }
                is.close();
                os.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return "Error while Uploading";
            }
        }
        return FileUploadPath + vLineIDPk + myfile.getFilename();
    }

    public String submitAction() {

        DCBindingContainer dcBinding = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding LineVO = (DCIteratorBinding) dcBinding.get("ElaajClaimDmsDocEOVO1Iterator");


        if (LineVO.getEstimatedRowCount() == 0) {
            //            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please attach document first.", "");
            //            FacesContext.getCurrentInstance().addMessage(null, msg);
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding = bindings.getOperationBinding("submitToBpmIPD");
            DCBindingContainer dcBindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
            operationBinding.execute();
            
        } else {

            String UploadFile = this.fileUploadActionNew();
            if (!"Failed".equalsIgnoreCase(UploadFile)) {
                BindingContainer bindings = getBindings();
                OperationBinding operationBinding = bindings.getOperationBinding("submitToBpmIPD");
                DCBindingContainer dcBindings =
                    (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
                operationBinding.execute();

                //                return "goBack";
                return "ClaimIntimationToHomePG";
            }
            
        }
        return null;
    }

    public String fileUploadActionNew() {

        int Err_Count = 0;
        BindingContainer bc = getBindings();
        OperationBinding ob = bc.getOperationBinding("UploadFile1");

        DCIteratorBinding hdIter = (DCIteratorBinding) getBindings().get("ElaajClaimIntimateEOVO1Iterator");
        DCIteratorBinding dcIter = (DCIteratorBinding) getBindings().get("ElaajClaimDmsDocEOVO1Iterator");
        ViewObject hvo = hdIter.getViewObject();
        ViewObject vo = dcIter.getViewObject();

        String DmsDocumentReference = null, vFileName = null, vUL_No = null;
        int vErr_Count = 0;

        try {
            vUL_No = hvo.getCurrentRow().getAttribute("ElaajClaimIntimateId").toString();
        } catch (Exception e) {
            vErr_Count = vErr_Count + 1;
        }

        if (vErr_Count > 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage Msg = new FacesMessage("Please Provide Valid Membership No");
            Msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, Msg);
        } else {
            for (Row row : vo.getAllRowsInRange()) {

                try {
                    DmsDocumentReference = hvo.getCurrentRow().getAttribute("DmsDocumentReference").toString();
                } catch (Exception e) {
                    DmsDocumentReference = "Create";
                }

                try {
                    vFileName = row.getAttribute("FileName").toString();
                } catch (Exception e) {
                    vFileName = "zzz.jpg";
                }

                ob.getParamsMap().put("DocumentReference", DmsDocumentReference);
                ob.getParamsMap().put("SchemaName", "DSS_HOSPITAL_CLAIM");
                ob.getParamsMap().put("RepoName", "DSS_HOSPITAL_CLAIM");
                ob.getParamsMap().put("PrimaryKey", "CLAIM_NO," + vUL_No);
                ob.getParamsMap().put("FileLocation", FileUploadPath);
                ob.getParamsMap().put("FileName", vFileName);
                ob.getParamsMap().put("Parameters", "CLAIM_NO," + vUL_No);
                //                System.out.println(ob.getParamsMap());

                Object result = ob.execute();
                //                System.out.println("Object Result >>>>" + result);

                if (!ob.getErrors().isEmpty()) {

                    Err_Count = Err_Count + 1;
                    break;


                } else {
                    hvo.getCurrentRow().setAttribute("DmsDocumentReference", result);
                    DmsDocumentReference = (String) result;
                }


            }

            if (Err_Count > 0) {
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage customMsg =
                    new FacesMessage("File Upload was Unsuccessfull. Please try again sometime later");
                customMsg.setSeverity(FacesMessage.SEVERITY_FATAL);
                context.addMessage(null, customMsg);
                return "Failed";
            }
        }


        return null;
    }

}
