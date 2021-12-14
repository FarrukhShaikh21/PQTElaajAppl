package pqt.gh.elaaj.view.request;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

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

import org.apache.myfaces.trinidad.model.UploadedFile;

public class BenefitDetailBean {
    private RichListView selectMaster;
    private RichInputFile rif;
    private String FileDownloadPath = "/u01/ADF_DMS/BTCS_CMS_FILE/DOWNLOAD/";
    private String FileUploadPath = "/u01/ADF_DMS/BTCS_CMS_FILE/UPLOAD/";
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
        if (vc.getNewValue() != null) {

            UploadedFile fileVal = (UploadedFile) vc.getNewValue();
            BindingContext bcx = BindingContext.getCurrent();
            BindingContainer bc = bcx.getCurrentBindingsEntry();
            DCBindingContainer dbc = (DCBindingContainer) bc;
            DCIteratorBinding iter = dbc.findIteratorBinding("ElaajClaimReimburstmentEOVO1Iterator");

            String vLineIDPk = null;
            //            vtime = (String)System.currentTimeMillis();
            Row row = iter.getCurrentRow();
            try {
                vLineIDPk = row.getAttribute("ElaajClaimReimburstmentId").toString();
            } catch (NullPointerException ex) {
                vLineIDPk = null;
            }
            //            String lineIdPk = null;
            String filePath = uploadFile(fileVal,"ElaajClaimReimburstmentEOVO1Iterator",vLineIDPk);
    //            row.setAttribute("FilePath", filePath);
            //            row.setAttribute("LineIdPk", lineIdPk);
            

            String fileName = fileVal.getFilename();
            row.setAttribute("AttachFile1", vLineIDPk + fileName);

            String fileType = fileVal.getContentType();
            fileUploadAction(vLineIDPk + fileName);
            getRif().resetValue();
    //            row.setAttribute("ContentType", fileType);
        }
    } 
     
    public void uploadFileEventClaimIntimation(ValueChangeEvent vc) {
        if (vc.getNewValue() != null) {

            UploadedFile fileVal = (UploadedFile) vc.getNewValue();
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

            String filePath = uploadFile(fileVal,"ElaajClaimIntimateEOVO1Iterator",vLineIDPk);
//            row.setAttribute("FilePath", filePath);
            //            row.setAttribute("LineIdPk", lineIdPk);
           
            String fileName = fileVal.getFilename();
            row.setAttribute("AttachFile1", vLineIDPk + fileName);

            String fileType = fileVal.getContentType();
            fileUploadAction(vLineIDPk + fileName);
            getRif().resetValue();
//            row.setAttribute("ContentType", fileType);
        }
    } 
 
    private String uploadFile(UploadedFile file,String pIteratorBinding, String pClaimId) {
        
            //        System.out.println("FileUploadPath: " + FileUploadPath);

//            BindingContext bcx = BindingContext.getCurrent();
//            BindingContainer bc = bcx.getCurrentBindingsEntry();
//            DCBindingContainer dbc = (DCBindingContainer) bc;
//            DCIteratorBinding iter = dbc.findIteratorBinding(pIteratorBinding);

            String vLineIDPk = pClaimId;
            //        row.setAttribute("LineIdPk", lineIdPk);

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
 
    public String fileUploadAction(String pFileName) {
    
    BindingContainer bc = getBindings();
    OperationBinding ob = bc.getOperationBinding("UploadFile1");

   
    ob.getParamsMap().put("DocumentReference", "Create");
    ob.getParamsMap().put("SchemaName", "FT_Elaaj_Claims");
    ob.getParamsMap().put("RepoName", "FT_Elaaj_Claims");
    ob.getParamsMap().put("PrimaryKey", "UL_NO," + "vUL_No");
    ob.getParamsMap().put("FileLocation", FileUploadPath);
    ob.getParamsMap().put("FileName", pFileName);
    ob.getParamsMap().put("Parameters", "UL_NO," + "vUL_No");
    Object result = ob.execute();
    
    return null;
    }


    public void setRif(RichInputFile rif) {
        this.rif = rif;
    }

    public RichInputFile getRif() {
        return rif;
    }

    public void setRif1(RichOutputFormatted rif1) {
        this.rif1 = rif1;
    }

    public RichOutputFormatted getRif1() {
        return rif1;
    }

    public void setRif2(RichInputFile rif2) {
        this.rif2 = rif2;
    }

    public RichInputFile getRif2() {
        return rif2;
    }
}
