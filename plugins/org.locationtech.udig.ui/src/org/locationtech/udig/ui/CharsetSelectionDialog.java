/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;

import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.internal.Messages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.SearchPattern;

public final class CharsetSelectionDialog extends FilteredItemsSelectionDialog {
    public class CharsetSelectionHistory extends SelectionHistory {

        @Override
        protected Object restoreItemFromMemento( IMemento memento ) {
            return null;
        }

        @Override
        protected void storeItemToMemento( Object item, IMemento memento ) {
        }

    }
    
    public CharsetSelectionDialog( Shell shell, boolean multi ) {
        this(shell, false, "*"); //$NON-NLS-1$
    }
    
    public CharsetSelectionDialog( Shell shell, boolean multi, String initialSelection ) {
        super(shell, multi);
        setTitle(Messages.CharsetSelectionDialog_title);
        setSelectionHistory(new CharsetSelectionHistory());
        setInitialPattern(initialSelection);
        
    }
    @Override
    protected IStatus validateItem( Object item ) {
        return Status.OK_STATUS;
    }
    @SuppressWarnings("unchecked")
    @Override
    protected Comparator getItemsComparator() {
        return new Comparator(){

            public int compare( Object o1, Object o2 ) {
                return ((Charset)o1).compareTo((Charset)o2);
            }
            
        };
    }
    @Override
    public String getElementName( Object item ) {
        return ((Charset)item).displayName();
    }
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = UiPlugin.getDefault().getDialogSettings();
        IDialogSettings section = settings.getSection("CharsetChange"); //$NON-NLS-1$
        if( section == null ){
            section = settings.addNewSection("CharsetChange"); //$NON-NLS-1$
        }
        return section; 
    }
    @Override
    protected void fillContentProvider( AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, IProgressMonitor progressMonitor ) throws CoreException {  
        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        progressMonitor.beginTask("Searching", charsets.size()); //$NON-NLS-1$

        
        for( Charset charset : charsets.values() ) {
            contentProvider.add(charset, itemsFilter);
            progressMonitor.worked(1);
        }
        progressMonitor.done();
        
    }
    @Override
    protected ItemsFilter createFilter() {
        SearchPattern searchPattern = new SearchPattern(){
            @Override
            public void setPattern( String stringPattern ) {
                if( stringPattern.length()==0)
                    super.setPattern("*"); //$NON-NLS-1$
                else
                    super.setPattern(stringPattern);
            }
        };
        ItemsFilter itemsFilter = new ItemsFilter(searchPattern){
            @Override
            public boolean isConsistentItem( final Object item ) {
                return true;
            }

            @Override
            public boolean matchItem( final Object item ) {
                return matches(((Charset)item).name())||matches(((Charset)item).displayName());
            }
            
        };
        
        
        return itemsFilter;
    }
    @Override
    protected Control createExtendedContentArea( Composite parent ) {
        return null;
    }
    public void setPattern( String value ) {
        setResult(Collections.singletonList(Charset.forName(value)));
    }
}
