package com.zkbp.nmr.da.diagram.celleditors;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

public class SpinnerCellEditor extends CellEditor {
    
    protected Spinner spinner;
    
    private ModifyListener modifyListener;
    
    /**
     * Style : has border。
     */
    private static final int defaultStyle = SWT.BORDER;
    
    public SpinnerCellEditor() {
        setStyle(defaultStyle);
    }
    
    public SpinnerCellEditor(Composite parent) {
        this(parent, defaultStyle);
    }
    
    SpinnerCellEditor(Composite parent, int style) {
        super(parent, style);
    }
    
    @Override protected Control createControl(Composite parent) {
        spinner = new Spinner(parent, getStyle());
        spinner.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetDefaultSelected(SelectionEvent e) {
                handleDefaultSelection(e);
            }
        });
        
        spinner.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201
            @Override public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
                if ((getControl() == null) || getControl().isDisposed()) {
                    return;
                }
            }
        });
        
        spinner.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                SpinnerCellEditor.this.focusLost(); // 当时去焦点时，退出编辑状态。
            }
        });
        spinner.addModifyListener(getModifyListener());
        return spinner;
    }
    
    protected void handleDefaultSelection(SelectionEvent e) {
        fireApplyEditorValue();
        deactivate();
    }
    
    protected void keyReleaseOccured(org.eclipse.swt.events.KeyEvent keyEvent) {
        if (keyEvent.character == '\r') {
            if (spinner != null && !spinner.isDisposed()) {
                if ((keyEvent.stateMask & SWT.CTRL) != 0) {
                    super.keyReleaseOccured(keyEvent);
                }
            }
            return;
        }
        super.keyReleaseOccured(keyEvent);
    }
    
    @Override protected Object doGetValue() {
        return spinner.getSelection();
    }
    
    @Override protected void doSetFocus() {
        if (spinner != null) {
            spinner.setFocus();
        }
    }
    
    @Override protected void doSetValue(Object value) {
        Assert.isTrue(spinner != null);
        spinner.removeModifyListener(getModifyListener());
        spinner.setSelection((int) value);
        spinner.addModifyListener(getModifyListener());
    }
    
    private ModifyListener getModifyListener() {
        if (modifyListener == null) {
            modifyListener = new ModifyListener() {
                
                @Override public void modifyText(ModifyEvent e) {
                    editOccured(e);
                }
            };
        }
        return modifyListener;
    }
    
    protected void editOccured(ModifyEvent e) {
        int value = Integer.valueOf(spinner.getText());
        boolean oldValidState = isValueValid();
        boolean newValidState = isCorrect(value);
        if (!newValidState) {
            System.out.println("超过边界");
        }
        valueChanged(oldValidState, newValidState);
    }
    
    @Override protected boolean isCorrect(Object value) {
        int v = (int) value;
        if (v < spinner.getMinimum() || v > spinner.getMaximum()) {
            return false;
        }
        return super.isCorrect(value);
    }
    
    @Override public LayoutData getLayoutData() {
        LayoutData data = new LayoutData();
        data.minimumWidth = 0;
        return data;
    }
    
    public void setMinimum(int minimum) {
        this.spinner.setMinimum(minimum);
    }
    
    public void setMaximum(int maximum) {
        spinner.setMaximum(maximum);
    }
    
    public void setSelection(int selection) {
        spinner.setSelection(selection);
    }
    
    public void setIncrement(int increment) {
        spinner.setIncrement(increment);
    }
    
    public void setPageIncrement(int pageIncrement) {
        spinner.setPageIncrement(pageIncrement);
    }
    
    @Override protected boolean dependsOnExternalFocusListener() {
        return false;
    }
}
