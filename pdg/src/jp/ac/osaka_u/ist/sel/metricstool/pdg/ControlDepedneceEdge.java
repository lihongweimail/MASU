package jp.ac.osaka_u.ist.sel.metricstool.pdg;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ConditionalBlockInfo;

/**
 * 制御依存辺を表すクラス
 * @author t-miyake
 *
 */
public class ControlDepedneceEdge extends PDGEdge {

    public ControlDepedneceEdge(final PDGControlNode fromNode, final PDGNode<?> toNode) {
        super(fromNode, toNode);
        
        if(!(fromNode.getCore() instanceof ConditionalBlockInfo)) {
            throw new IllegalArgumentException("fromNode is not a control statement.");
        }
    }
    
}
