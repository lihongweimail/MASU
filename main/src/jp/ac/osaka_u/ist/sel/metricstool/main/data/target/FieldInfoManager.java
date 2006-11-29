package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * �t�B�[���h�����Ǘ�����N���X�D FieldInfo ��v�f�Ƃ��Ď��D
 * 
 * @author y-higo
 * 
 */
public final class FieldInfoManager implements Iterable<FieldInfo> {

    /**
     * �t�B�[���h�����Ǘ����Ă���C���X�^���X��Ԃ��D �V���O���g���p�^�[���������Ă���D
     * 
     * @return �t�B�[���h�����Ǘ����Ă���C���X�^���X
     */
    public static FieldInfoManager getInstance() {
        return SINGLETON;
    }

    /**
     * �t�B�[���h����ǉ�����
     * 
     * @param fieldInfo �ǉ�����t�B�[���h���
     */
    public void add(final FieldInfo fieldInfo) {

        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == fieldInfo) {
            throw new NullPointerException();
        }

        this.fieldInfos.add(fieldInfo);
    }

    /**
     * �t�B�[���h���� Iterator ��Ԃ��D���� Iterator �� unmodifiable �ł���C�ύX������s�����Ƃ͂ł��Ȃ��D
     */
    public Iterator<FieldInfo> iterator() {
        SortedSet<FieldInfo> unmodifiableFieldInfos = Collections
                .unmodifiableSortedSet(this.fieldInfos);
        return unmodifiableFieldInfos.iterator();
    }

    /**
     * �����Ă���t�B�[���h�̌���Ԃ�
     * @return �t�B�[���h�̌�
     */
    public int getFieldCount() {
        return this.fieldInfos.size();
    }

    /**
     * 
     * �R���X�g���N�^�D �V���O���g���p�^�[���Ŏ������Ă��邽�߂� private �����Ă���D
     */
    private FieldInfoManager() {
        this.fieldInfos = new TreeSet<FieldInfo>();
    }

    /**
     * 
     * �V���O���g���p�^�[�����������邽�߂̕ϐ��D
     */
    private static final FieldInfoManager SINGLETON = new FieldInfoManager();

    /**
     * 
     * �t�B�[���h��� (FieldInfo) ���i�[����ϐ��D
     */
    private final SortedSet<FieldInfo> fieldInfos;
}