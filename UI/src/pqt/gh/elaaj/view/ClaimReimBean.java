package pqt.gh.elaaj.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;


import oracle.jbo.Row;

import oracle.jbo.ViewObject;

import oracle.jbo.domain.BlobDomain;

import org.apache.myfaces.trinidad.model.UploadedFile;

public class ClaimReimBean {
    private RichInputFile if1;
    private String FileDownloadPath = "/u01/ADF_DMS/URC_FILE/DOWNLOAD/";
    private String FileUploadPath = "/u01/ADF_DMS/URC_FILE/UPLOAD/";
    //private String FileUploadPath = "D:\\Forms\\";

    public ClaimReimBean() {
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void dmsDocReimUploadFileEvent(ValueChangeEvent vc) {
        // Add event code here...
        if (vc.getNewValue() != null) {
            UploadedFile fileVal = (UploadedFile) vc.getNewValue();
            BindingContext bcx = BindingContext.getCurrent();
            BindingContainer bc = bcx.getCurrentBindingsEntry();
            DCBindingContainer dbc = (DCBindingContainer) bc;
            DCIteratorBinding iter = dbc.findIteratorBinding("ElaajClaimReimDmsDocEOVO1Iterator");

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

    private String uploadFile1(UploadedFile file) {
        BindingContext bcx = BindingContext.getCurrent();
        BindingContainer bc = bcx.getCurrentBindingsEntry();
        DCBindingContainer dbc = (DCBindingContainer) bc;
        DCIteratorBinding iter = dbc.findIteratorBinding("ElaajClaimReimDmsDocEOVO1Iterator");

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
        DCIteratorBinding LineVO = (DCIteratorBinding) dcBinding.get("ElaajClaimReimDmsDocEOVO1Iterator");

        if (LineVO.getEstimatedRowCount() == 0) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding = bindings.getOperationBinding("submitToBpmOPD");
            DCBindingContainer dcBindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
            operationBinding.execute();
        } else {
            String UploadFile = this.fileUploadActionNew();
            if (!"Failed".equalsIgnoreCase(UploadFile)) {
                BindingContainer bindings = getBindings();
                OperationBinding operationBinding = bindings.getOperationBinding("submitToBpmOPD");
                DCBindingContainer dcBindings =
                    (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
                operationBinding.execute();
                return "reimbursementToHome";
            }
            
        }
        return null;
    }

    public String fileUploadActionNew() {

        int Err_Count = 0;
        BindingContainer bc = getBindings();
        OperationBinding ob = bc.getOperationBinding("UploadFile1");

        DCIteratorBinding hdIter = (DCIteratorBinding) getBindings().get("ElaajClaimReimburstmentEOVO1Iterator");
        DCIteratorBinding dcIter = (DCIteratorBinding) getBindings().get("ElaajClaimReimDmsDocEOVO1Iterator");
        ViewObject hvo = hdIter.getViewObject();
        ViewObject vo = dcIter.getViewObject();

        String DmsDocumentReference = null, vFileName = null, vUL_No = null;
        int vErr_Count = 0;

        try {
            vUL_No = hvo.getCurrentRow().getAttribute("ElaajClaimReimburstmentId").toString();
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

    public void setIf1(RichInputFile if1) {
        this.if1 = if1;
    }

    public RichInputFile getIf1() {
        return if1;
    }


}
