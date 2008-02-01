package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.FieldInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetInnerClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetMethodInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.UnknownTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.external.ExternalClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * �������Q�ƌ^��\���N���X
 * 
 * @author y-higo
 * 
 */
public class UnresolvedClassTypeInfo implements UnresolvedReferenceTypeInfo {

    /**
     * ���p�\�Ȗ��O��Ԗ��C�Q�Ɩ���^���ď�����
     * 
     * @param availableNamespaces ���O��Ԗ�
     * @param referenceName �Q�Ɩ�
     */
    public UnresolvedClassTypeInfo(final AvailableNamespaceInfoSet availableNamespaces,
            final String[] referenceName) {

        // �s���ȌĂяo���łȂ������`�F�b�N
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == availableNamespaces) || (null == referenceName)) {
            throw new NullPointerException();
        }

        this.availableNamespaceSet = availableNamespaces;
        this.referenceName = referenceName;
        //this.fullReferenceName = referenceName;
        //this.ownerType = null;
        this.typeParameterUsages = new LinkedList<UnresolvedClassTypeInfo>();
    }

    @Override
    public boolean alreadyResolved() {
        return null != this.resolvedInfo;
    }

    @Override
    public TypeInfo getResolvedType() {

        if (!this.alreadyResolved()) {
            throw new NotResolvedException();
        }

        return this.resolvedInfo;
    }

    @Override
    public TypeInfo resolveType(TargetClassInfo usingClass, TargetMethodInfo usingMethod,
            ClassInfoManager classInfoManager, FieldInfoManager fieldInfoManager,
            MethodInfoManager methodInfoManager) {

        // �s���ȌĂяo���łȂ������`�F�b�N
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == classInfoManager) {
            throw new NullPointerException();
        }

        // ���ɉ����ς݂ł���ꍇ�́C�L���b�V����Ԃ�
        if (this.alreadyResolved()) {
            return this.getResolvedType();
        }

        //�@�P���Q�Ƃ̏ꍇ
        if (this.isMoniminalReference()) {

            //�@�C���|�[�g����Ă���p�b�P�[�W���̃N���X���猟��
            for (final AvailableNamespaceInfo availableNamespace : this.getAvailableNamespaces()) {

                // import aaa.bbb.*�̏ꍇ (�N���X���̕�����*)
                if (availableNamespace.isAllClasses()) {

                    //�@���p�\�ȃN���X�ꗗ���擾���C�������猟��
                    final String[] namespace = availableNamespace.getNamespace();
                    for (final ClassInfo availableClass : classInfoManager.getClassInfos(namespace)) {

                        //�@�Q�Ƃ���Ă���N���X����������
                        if (this.referenceName[0].equals(availableClass.getClassName())) {
                            // TODO �^�p�����[�^�̏���ۑ����鏈�����K�v
                            this.resolvedInfo = new ClassTypeInfo(availableClass);
                            return this.resolvedInfo;
                        }
                    }

                    // import aaa.bbb.CCC�̏ꍇ�@(�N���X���܂ŋL�q����Ă���)
                } else {

                    ClassInfo referencedClass = classInfoManager.getClassInfo(availableNamespace
                            .getImportName());
                    // null �̏ꍇ�͊O���N���X�̎Q�ƂƂ݂Ȃ�
                    if (null == referencedClass) {
                        referencedClass = new ExternalClassInfo(availableNamespace.getImportName());
                        classInfoManager.add((ExternalClassInfo) referencedClass);
                    }

                    // TODO�@�^�p�����[�^�̏����i�[���鏈�����K�v
                    this.resolvedInfo = new ClassTypeInfo(referencedClass);
                    return this.resolvedInfo;
                }
            }

            // �f�t�H���g�p�b�P�[�W����N���X������
            for (final ClassInfo availableClass : classInfoManager.getClassInfos(new String[0])) {

                // �Q�Ƃ���Ă���N���X����������
                if (this.referenceName[0].equals(availableClass.getClassName())) {
                    // TODO�@�^�p�����[�^�̏���ۑ����鏈�����K�v
                    this.resolvedInfo = new ClassTypeInfo(availableClass);
                    return this.resolvedInfo;
                }
            }

            // �������Q�Ƃ̏ꍇ
        } else {

            //�@�C���|�[�g����Ă���N���X�̎q�N���X���猟��
            AVAILABLENAMESPACE: for (final AvailableNamespaceInfo availableNamespace : this
                    .getAvailableNamespaces()) {

                // import aaa.bbb.*�̏ꍇ (�N���X���̕�����*)
                if (availableNamespace.isAllClasses()) {

                    // ���p�\�ȃN���X�ꗗ���擾���C�������猟��
                    final String[] namespace = availableNamespace.getNamespace();
                    for (final ClassInfo availableClass : classInfoManager.getClassInfos(namespace)) {

                        //�@�Q�Ƃ���Ă���N���X����������
                        if (this.referenceName[0].equals(availableClass.getClassName())) {

                            // �ΏۃN���X�łȂ��ꍇ�͓����N���X���͂킩��Ȃ��̂ŃX�L�b�v
                            if (!(availableClass instanceof TargetClassInfo)) {
                                continue AVAILABLENAMESPACE;
                            }

                            // �ΏۃN���X�̏ꍇ�́C���ɓ����N���X�����ǂ��čs��
                            TargetClassInfo currentClass = (TargetClassInfo) availableClass;
                            INDEX: for (int index = 1; index < this.referenceName.length; index++) {
                                final SortedSet<TargetInnerClassInfo> innerClasses = currentClass
                                        .getInnerClasses();
                                for (final TargetInnerClassInfo innerClass : innerClasses) {

                                    if (this.referenceName[index].equals(innerClass.getClassName())) {
                                        currentClass = innerClass;
                                        continue INDEX;
                                    }

                                    // �����ɓ��B����̂́C�N���X��������Ȃ������ꍇ
                                    this.resolvedInfo = UnknownTypeInfo.getInstance();
                                    return this.resolvedInfo;
                                }
                            }

                            //�@�����ɓ��B����̂́C�N���X�����������ꍇ
                            this.resolvedInfo = new ClassTypeInfo(currentClass);
                            // TODO �^�p�����[�^�̏������K�v
                            return this.resolvedInfo;
                        }
                    }

                    // import aaa.bbb.CCC�̏ꍇ (�N���X���܂ŋL�q����Ă���)
                } else {

                    ClassInfo importClass = classInfoManager.getClassInfo(availableNamespace
                            .getImportName());

                    //�@null �̏ꍇ�͂���(�O��)�N���X��\���I�u�W�F�N�g���쐬 
                    if (null == importClass) {
                        importClass = new ExternalClassInfo(availableNamespace.getImportName());
                        classInfoManager.add((ExternalClassInfo) importClass);
                    }

                    // importClass���ΏۃN���X�łȂ��ꍇ�͓����N���X��񂪂킩��Ȃ��̂ŃX�L�b�v
                    if (!(importClass instanceof TargetClassInfo)) {
                        continue AVAILABLENAMESPACE;
                    }

                    // �ΏۃN���X�̏ꍇ�́C���ɓ����N���X�����ǂ��čs��
                    TargetClassInfo currentClass = (TargetClassInfo) importClass;
                    INDEX: for (int index = 1; index < this.referenceName.length; index++) {
                        final SortedSet<TargetInnerClassInfo> innerClasses = currentClass
                                .getInnerClasses();
                        for (final TargetInnerClassInfo innerClass : innerClasses) {

                            if (this.referenceName[index].equals(innerClass.getClassName())) {
                                currentClass = innerClass;
                                continue INDEX;
                            }

                            // �����ɓ��B����̂́C�N���X��������Ȃ������ꍇ
                            this.resolvedInfo = UnknownTypeInfo.getInstance();
                            return this.resolvedInfo;
                        }
                    }

                    //�@�����ɓ��B����̂́C�N���X�����������ꍇ
                    this.resolvedInfo = new ClassTypeInfo(currentClass);
                    // TODO �^�p�����[�^�̏������K�v
                    return this.resolvedInfo;
                }
            }
        }

        this.resolvedInfo = UnknownTypeInfo.getInstance();
        return this.resolvedInfo;
    }

    /**
     * ���p�\�Ȗ��O��ԁC�^�̊��S�C������^���ď�����
     * @param referenceName �^�̊��S�C����
     */
    public UnresolvedClassTypeInfo(final String[] referenceName) {
        this(new AvailableNamespaceInfoSet(), referenceName);
    }

    ///**
    // * ���p�\�Ȗ��O��Ԗ��C�Q�Ɩ���^���ď�����
    // * 
    // * @param availableNamespaces ���O��Ԗ�
    // * @param referenceName �Q�Ɩ�
    // */
    /*
    public UnresolvedReferenceTypeInfo(final AvailableNamespaceInfoSet availableNamespaces,
            final String[] referenceName, final UnresolvedReferenceTypeInfo ownerType) {

        // �s���ȌĂяo���łȂ������`�F�b�N
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == availableNamespaces) || (null == referenceName) || (null == ownerType)) {
            throw new NullPointerException();
        }

        this.availableNamespaceSet = availableNamespaces;
        String[] ownerReferenceName = ownerType.getFullReferenceName();
        String[] fullReferenceName = new String[referenceName.length+ownerReferenceName.length];
        System.arraycopy(ownerReferenceName, 0, fullReferenceName, 0, ownerReferenceName.length);
        System.arraycopy(referenceName, 0, fullReferenceName, ownerReferenceName.length, referenceName.length);
        this.fullReferenceName = fullReferenceName;
        this.referenceName = referenceName;
        this.ownerType = ownerType;
        this.typeParameterUsages = new LinkedList<UnresolvedReferenceTypeInfo>();
    }
    */
    /**
     * �^�p�����[�^�g�p��ǉ�����
     * 
     * @param typeParameterUsage �ǉ�����^�p�����[�^�g�p
     */
    public final void addTypeArgument(final UnresolvedClassTypeInfo typeParameterUsage) {

        // �s���ȌĂяo���łȂ������`�F�b�N
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == typeParameterUsage) {
            throw new NullPointerException();
        }

        this.typeParameterUsages.add(typeParameterUsage);
    }

    /**
     * ���̃N���X�Q�ƂŎg�p����Ă���^�p�����[�^�� List ��Ԃ�
     * 
     * @return ���̃N���X�Q�ƂŎg�p����Ă���^�p�����[�^�� List
     */
    public final List<UnresolvedClassTypeInfo> getTypeArguments() {
        return Collections.unmodifiableList(this.typeParameterUsages);
    }

    /**
     * ���̎Q�ƌ^�̖��O��Ԃ�
     * 
     * @return ���̎Q�ƌ^�̖��O��Ԃ�
     */
    public final String getTypeName() {
        return this.referenceName[this.referenceName.length - 1];
    }

    ///**
    // * ���̎Q�ƌ^��owner���܂߂��Q�Ɩ���Ԃ�
    // * 
    // * @return ���̎Q�ƌ^��owner���܂߂��Q�Ɩ���Ԃ�
    // */
    /*public final String[] getFullReferenceName() {
        return this.fullReferenceName;
    }*/

    /**
     * ���̎Q�ƌ^�̎Q�Ɩ���Ԃ�
     * 
     * @return ���̎Q�ƌ^�̎Q�Ɩ���Ԃ�
     */
    public final String[] getReferenceName() {
        return this.referenceName;
    }

    ///**
    // * ���̎Q�ƌ^���������Ă��関�����Q�ƌ^��Ԃ�
    // * 
    // * @return ���̎Q�ƌ^���������Ă��関�����Q�ƌ^
    // */
    /*public final UnresolvedReferenceTypeInfo getOwnerType() {
        return this.ownerType;
    }*/

    ///**
    // * ���̎Q�ƌ^���C���̎Q�ƌ^�ɂ������Ă��邩�ǂ�����Ԃ�
    // * 
    // * @return �������Ă���ꍇ�� true�C�������Ă��Ȃ��ꍇ�� false
    // */
    /*public final boolean hasOwnerReference() {
        return null != this.ownerType;
    }*/

    /**
     * ���̎Q�ƌ^�̎Q�Ɩ��������ŗ^����ꂽ�����Ō������ĕԂ�
     * 
     * @param delimiter �����ɗp���镶��
     * @return ���̎Q�ƌ^�̎Q�Ɩ��������ŗ^����ꂽ�����Ō�������������
     */
    public final String getReferenceName(final String delimiter) {

        if (null == delimiter) {
            throw new NullPointerException();
        }

        final StringBuilder sb = new StringBuilder(this.referenceName[0]);
        for (int i = 1; i < this.referenceName.length; i++) {
            sb.append(delimiter);
            sb.append(this.referenceName[i]);
        }

        return sb.toString();
    }

    /**
     * ���̎Q�ƌ^�̊��S���薼�Ƃ��ĉ\���̂��閼�O��Ԗ��̈ꗗ��Ԃ�
     * 
     * @return ���̎Q�ƌ^�̊��S���薼�Ƃ��ĉ\���̂��閼�O��Ԗ��̈ꗗ
     */
    public final AvailableNamespaceInfoSet getAvailableNamespaces() {
        return this.availableNamespaceSet;
    }

    /**
     * ���̎Q�Ƃ��P�����ǂ�����Ԃ�
     * 
     * @return�@�P���ł���ꍇ��true�C�����łȂ��ꍇ��false
     */
    public final boolean isMoniminalReference() {
        return 1 == this.referenceName.length;
    }

    public final static UnresolvedClassTypeInfo getInstance(UnresolvedClassInfo referencedClass) {
        return new UnresolvedClassTypeInfo(referencedClass.getFullQualifiedName());
    }

    /**
     * ���p�\�Ȗ��O��Ԗ���ۑ����邽�߂̕ϐ��C���O���������̍ۂɗp����
     */
    private final AvailableNamespaceInfoSet availableNamespaceSet;

    /**
     * �Q�Ɩ���ۑ�����ϐ�
     */
    private final String[] referenceName;

    ///**
    // * owner���܂߂��Q�Ɩ���ۑ�����ϐ�
    // */
    //private final String[] fullReferenceName;

    ///**
    // * ���̎Q�Ƃ��������Ă��関�����Q�ƌ^��ۑ�����ϐ�
    // */
    //private final UnresolvedReferenceTypeInfo ownerType;

    /**
     * �^�����Q�Ƃ�ۑ����邽�߂̕ϐ�
     */
    private final List<UnresolvedClassTypeInfo> typeParameterUsages;

    private TypeInfo resolvedInfo;

}