package jp.ac.osaka_u.ist.sdl.scdetector;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.BlockInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ConditionInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ConditionalBlockInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ConditionalClauseInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ExecutableElementInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.FieldInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.LocalSpaceInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.SingleStatementInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.StatementInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.VariableInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.VariableUsageInfo;


public class ProgramSlice {

    /**
     * ��(statementA�CstatementB)����_�Ƃ��ăo�b�N���[�h�X���C�X���s���C
     * �X���C�X�Ɍ����n�b�V���l�̈�v���镶���N���[�����\�����镶�Ƃ��Ď擾����
     * 
     * @param elementA
     * @param elementB
     * @param clonePair
     * @param assignedVariableHashes
     * @param elementHash
     * @param usedVariableHashesA
     * @param usedVariableHashesB
     */
    static void performBackwordSlice(final ExecutableElementInfo elementA,
            final ExecutableElementInfo elementB, final ClonePairInfo clonePair,
            final Map<VariableInfo<?>, Set<ExecutableElementInfo>> assignedVariableHashes,
            final Map<ExecutableElementInfo, Integer> elementHash,
            final Set<VariableInfo<?>> usedVariableHashesA,
            final Set<VariableInfo<?>> usedVariableHashesB) {

        // �X���C�X��_�̕ϐ����p���擾
        final Set<VariableUsageInfo<?>> variableUsagesA = elementA.getVariableUsages();
        final Set<VariableUsageInfo<?>> variableUsagesB = elementB.getVariableUsages();

        // �X���C�X�ǉ��p��Set��錾
        final SortedSet<ExecutableElementInfo> relatedElementsA = new TreeSet<ExecutableElementInfo>();
        final SortedSet<ExecutableElementInfo> relatedElementsB = new TreeSet<ExecutableElementInfo>();

        //�@�X���C�X��_(elementA)�̕ϐ����p���Q�Ƃł���΁C���̕ϐ��ɑ΂��đ�����s���Ă��镶���X���C�X�ǉ��p��Set�Ɋi�[����
        for (final VariableUsageInfo<?> variableUsage : variableUsagesA) {
            if (variableUsage.isReference()) {
                final VariableInfo<?> usedVariable = variableUsage.getUsedVariable();
                if (!usedVariableHashesA.contains(usedVariable)
                        && !(usedVariable instanceof FieldInfo)
                        && assignedVariableHashes.containsKey(usedVariable)) {
                    relatedElementsA.addAll(assignedVariableHashes.get(usedVariable));
                    usedVariableHashesA.add(usedVariable);
                }
            }
        }

        //�@�X���C�X��_(elementB)�̕ϐ����p���Q�Ƃł���΁C���̕ϐ��ɑ΂��đ�����s���Ă��镶���X���C�X�ǉ��p��Set�Ɋi�[����
        for (final VariableUsageInfo<?> variableUsage : variableUsagesB) {
            final VariableInfo<?> usedVariable = variableUsage.getUsedVariable();
            if (variableUsage.isReference()) {
                if (!usedVariableHashesB.contains(usedVariable)
                        && !(usedVariable instanceof FieldInfo)
                        && assignedVariableHashes.containsKey(usedVariable)) {
                    relatedElementsB.addAll(assignedVariableHashes.get(usedVariable));
                    usedVariableHashesB.add(usedVariable);
                }
            }
        }

        final ExecutableElementInfo[] relatedElementArrayA = relatedElementsA
                .toArray(new ExecutableElementInfo[] {});
        final ExecutableElementInfo[] relatedElementArrayB = relatedElementsB
                .toArray(new ExecutableElementInfo[] {});

        for (int a = 0; a < relatedElementArrayA.length; a++) {

            if (relatedElementArrayA[a] == elementA) {
                break;
            }

            for (int b = 0; b < relatedElementArrayB.length; b++) {

                if (relatedElementArrayB[b] == elementB) {
                    break;
                }

                if ((relatedElementArrayA[a] instanceof SingleStatementInfo)
                        && (relatedElementArrayB[b] instanceof SingleStatementInfo)) {

                    final int hashA = elementHash.get(relatedElementArrayA[a]);
                    final int hashB = elementHash.get(relatedElementArrayB[b]);

                    if (hashA == hashB) {
                        clonePair.add(relatedElementArrayA[a], relatedElementArrayB[b]);

                        ProgramSlice.performBackwordSlice(relatedElementArrayA[a],
                                relatedElementArrayB[b], clonePair, assignedVariableHashes,
                                elementHash, usedVariableHashesA, usedVariableHashesB);
                    }

                } else if ((relatedElementArrayA[a] instanceof ConditionInfo)
                        && (relatedElementArrayB[b] instanceof ConditionInfo)) {

                    final ConditionInfo conditionA = (ConditionInfo) relatedElementArrayA[a];
                    final ConditionInfo conditionB = (ConditionInfo) relatedElementArrayB[b];

                    final int hashA = conditionA.getText().hashCode();
                    final int hashB = conditionB.getText().hashCode();

                    if (hashA == hashB) {
                        clonePair.add(conditionA, conditionB);

                        ProgramSlice.performForwardSlice(conditionA, conditionB, clonePair,
                                assignedVariableHashes, elementHash, usedVariableHashesA,
                                usedVariableHashesB);
                    }
                }
            }
        }
    }

    static void performForwardSlice(final ConditionInfo conditionA, final ConditionInfo conditionB,
            final ClonePairInfo clonePair,
            final Map<VariableInfo<?>, Set<ExecutableElementInfo>> variableUsageHashes,
            final Map<ExecutableElementInfo, Integer> statementHash,
            final Set<VariableInfo<?>> usedVariableHashesA,
            final Set<VariableInfo<?>> usedVariableHashesB) {

        final Set<VariableUsageInfo<?>> variableUsagesA = conditionA.getVariableUsages();
        final Set<VariableUsageInfo<?>> variableUsagesB = conditionB.getVariableUsages();

        final Set<VariableInfo<?>> usedVariablesA = new HashSet<VariableInfo<?>>();
        for (final VariableUsageInfo<?> variableUsage : variableUsagesA) {
            final VariableInfo<?> variable = variableUsage.getUsedVariable();
            usedVariablesA.add(variable);
        }

        final Set<VariableInfo<?>> usedVariablesB = new HashSet<VariableInfo<?>>();
        for (final VariableUsageInfo<?> variableUsage : variableUsagesB) {
            final VariableInfo<?> variable = variableUsage.getUsedVariable();
            usedVariablesB.add(variable);
        }

        final ConditionalBlockInfo ownerBlockA = CONDITION_MAP.get(conditionA);
        final ConditionalBlockInfo ownerBlockB = CONDITION_MAP.get(conditionB);

        final SortedSet<ExecutableElementInfo> innerElementA = ProgramSlice
                .getAllInnerExecutableElementInfo(ownerBlockA);
        final SortedSet<ExecutableElementInfo> innerElementB = ProgramSlice
                .getAllInnerExecutableElementInfo(ownerBlockB);

        final ExecutableElementInfo[] innerElementArrayA = innerElementA
                .toArray(new ExecutableElementInfo[] {});
        final ExecutableElementInfo[] innerElementArrayB = innerElementB
                .toArray(new ExecutableElementInfo[] {});

        for (int i = 0; i < innerElementArrayA.length; i++) {
            for (int j = 0; j < innerElementArrayB.length; j++) {

                final int hashA = statementHash.get(innerElementArrayA[i]);
                final int hashB = statementHash.get(innerElementArrayB[j]);

                if ((hashA == hashB) && ProgramSlice.isUsed(usedVariablesA, innerElementArrayA[i])
                        && ProgramSlice.isUsed(usedVariablesB, innerElementArrayB[j])) {

                    clonePair.add(innerElementArrayA[i], innerElementArrayB[j]);
                }
            }
        }
    }

    private static boolean isUsed(final Set<VariableInfo<?>> variables,
            final ExecutableElementInfo ExecutableElementInfo) {

        final Set<VariableUsageInfo<?>> variableUsages = ExecutableElementInfo.getVariableUsages();
        for (final VariableUsageInfo<?> variableUsage : variableUsages) {
            final VariableInfo<?> usedVariable = variableUsage.getUsedVariable();
            if (variables.contains(usedVariable)) {
                return true;
            }
        }

        return false;
    }

    private static SortedSet<ExecutableElementInfo> getAllInnerExecutableElementInfo(
            final BlockInfo block) {

        final SortedSet<ExecutableElementInfo> elements = new TreeSet<ExecutableElementInfo>();
        for (final StatementInfo statement : block.getStatements()) {
            if (statement instanceof SingleStatementInfo) {
                elements.add(statement);
            } else if (statement instanceof BlockInfo) {
                elements.addAll(ProgramSlice
                        .getAllInnerExecutableElementInfo((BlockInfo) statement));
            }
        }

        return elements;
    }

    static Map<ConditionInfo, ConditionalBlockInfo> CONDITION_MAP = new HashMap<ConditionInfo, ConditionalBlockInfo>();

    static void makeConditionMap(final LocalSpaceInfo localSpace) {

        // ConditionalBlockInfo�̎q�N���X�ł���΁CMap�Ƀf�[�^��ǉ�
        if (localSpace instanceof ConditionalBlockInfo) {
            final ConditionalBlockInfo conditionalBlockInfo = ((ConditionalBlockInfo) localSpace);
            final ConditionalClauseInfo conditionalClauseInfo = conditionalBlockInfo
                    .getConditionalClause();
            final ConditionInfo condition = conditionalClauseInfo.getCondition();
            CONDITION_MAP.put(condition, (ConditionalBlockInfo) localSpace);
        }

        final Set<StatementInfo> innerStatements = localSpace.getStatements();
        for (final StatementInfo innerStatement : innerStatements) {
            if (innerStatement instanceof LocalSpaceInfo) {
                makeConditionMap((LocalSpaceInfo) innerStatement);
            }
        }

    }
}