package pqt.gh.elaaj.view.backing;

import java.io.IOException;

import javax.el.ELContext;

import javax.el.MethodExpression;

import oracle.adf.view.rich.component.rich.data.RichListView;
import oracle.adf.view.rich.component.rich.nav.RichButton;

import oracle.adf.view.rich.component.rich.output.RichOutputFormatted;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.adf.model.BindingContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.servlet.http.HttpSession;

import org.apache.myfaces.trinidad.event.SelectionEvent;

public class LoginBean {
    private RichButton b1;

    public LoginBean() {
    }


    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public String LoginVerification() {

        BindingContainer bindings = getBindings();
        OperationBinding operationBinding = bindings.getOperationBinding("VerifyUserAuth1");
        Object result = operationBinding.execute();
        String Output = result.toString();
        return Output;
    }
    
    
//    public String LogoutAction(ActionEvent actionEvent) {
//        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
//        //HttpServletResponse response = (HttpServletResponse)ectx.getResponse();
//        HttpSession session = (HttpSession) ectx.getSession(false);
//        session.invalidate();
//        //response.sendRedirect("Login");
//
//        FacesContext fctx = FacesContext.getCurrentInstance();
//        String contextPath = fctx.getExternalContext().getRequestContextPath();
//        try {
//            fctx.getExternalContext().redirect(contextPath + "/faces/Login");
//        } catch (IOException e) {
//        }
//        fctx.responseComplete();
//        return null;
//    }

    public void setB1(RichButton b1) {
        this.b1 = b1;
    }

    public RichButton getB1() {
        return b1;
    }


    public String LogoutAction() {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        //HttpServletResponse response = (HttpServletResponse)ectx.getResponse();
        HttpSession session = (HttpSession) ectx.getSession(false);
        session.invalidate();
        //response.sendRedirect("Login");

        FacesContext fctx = FacesContext.getCurrentInstance();
        String contextPath = fctx.getExternalContext().getRequestContextPath();
        try {
            fctx.getExternalContext().redirect(contextPath + "/faces/Login");
        } catch (IOException e) {
        }
        fctx.responseComplete();
        return null;
    }
}
