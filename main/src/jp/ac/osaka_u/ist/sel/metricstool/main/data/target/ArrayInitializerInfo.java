package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * �z��̏�������\���N���X
 * 
 * @author t-miyake
 *
 */
@SuppressWarnings("serial")
public final class ArrayInitializerInfo extends ExpressionInfo {

    /**
     * �I�u�W�F�N�g��������
     * 
     * @param elements ��������
     * @param ownerMethod �I�[�i�[���\�b�h
     * @param fromLine �J�n�s
     * @param fromColumn �J�n��
     * @param toLine �I���s
     * @param toColumn �I����
     */
    public ArrayInitializerInfo(List<ExpressionInfo> elements, final CallableUnitInfo ownerMethod,
            final int fromLine, final int fromColumn, final int toLine, final int toColumn) {
        super(ownerMethod, fromLine, fromColumn, toLine, toColumn);

        if (null == elements) {
            throw new IllegalArgumentException("elements is null");
        }
        this.elementInitialiers = Collections.unmodifiableList(elements);

        for (final ExpressionInfo element : this.elementInitialiers) {
            element.setOwnerExecutableElement(this);
        }
    }

    /**
     * �v�f�̏���������Ԃ�
     * 
     * @return �v�f�̏�������
     */
    public List<ExpressionInfo> getElementInitializers() {
        return this.elementInitialiers;
    }

    /**
     * �z��̒�����Ԃ�
     * 
     * @return�@�z��̒���
     */
    public final int getArrayLength() {
        return this.elementInitialiers.size();
    }

    @Override
    public String getText() {
        final StringBuilder text = new StringBuilder();
        text.append("{");

        final Iterator<ExpressionInfo> elements = this.elementInitialiers.iterator();
        if (elements.hasNext()) {
            text.append(elements.next().getText());
        }
        while (elements.hasNext()) {
            text.append(", ");
            text.append(elements.next().getText());
        }

        text.append("}");
        return text.toString();
    }

    @Override
    public TypeInfo getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> getVariableUsages() {
        final Set<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> usages = new TreeSet<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>>();
        for (final ExpressionInfo element : this.getElementInitializers()) {
            usages.addAll(element.getVariableUsages());
        }
        return Collections.unmodifiableSet(usages);
    }

    /**
     * �Ăяo����Set��Ԃ�
     * 
     * @return �Ăяo����Set
     */
    public Set<CallInfo<?>> getCalls() {
        final Set<CallInfo<?>> calls = new HashSet<CallInfo<?>>();
        for (final ExpressionInfo element : this.getElementInitializers()) {
            calls.addAll(element.getCalls());
        }
        return Collections.unmodifiableSet(calls);
    }

    private final List<ExpressionInfo> elementInitialiers;

}