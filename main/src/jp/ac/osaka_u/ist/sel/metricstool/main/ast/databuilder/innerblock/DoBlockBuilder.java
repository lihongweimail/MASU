package jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.innerblock;

import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.BuildDataManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.statemanager.innerblock.DoBlockStateManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedDoBlockInfo;

public class DoBlockBuilder extends ConditionalBlockBuilder {

    public DoBlockBuilder(BuildDataManager targetDataManager) {
        super(targetDataManager);

        this.blockStateManager = new DoBlockStateManager();
    }

    @Override
    protected UnresolvedDoBlockInfo createUnresolvedBlockInfo() {
        return new UnresolvedDoBlockInfo();
    }

}