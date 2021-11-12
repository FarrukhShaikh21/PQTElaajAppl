package pqt.gh.elaaj.view.request;

import javax.el.ELContext;

import javax.el.MethodExpression;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;

import oracle.adf.view.rich.component.rich.data.RichListView;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

public class BenefitDetailBean {
    private RichListView selectMaster;

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
}
