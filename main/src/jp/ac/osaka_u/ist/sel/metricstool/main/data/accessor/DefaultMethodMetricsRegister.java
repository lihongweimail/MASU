package jp.ac.osaka_u.ist.sel.metricstool.main.data.accessor;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.metric.MethodMetricsInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.metric.MetricAlreadyRegisteredException;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.plugin.AbstractPlugin;
import jp.ac.osaka_u.ist.sel.metricstool.main.plugin.AbstractPlugin.PluginInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.util.METRIC_TYPE;


/**
 * �v���O�C�������\�b�h���g���N�X��o�^���邽�߂ɗp����N���X�D
 * 
 * @author y-higo
 */
public class DefaultMethodMetricsRegister implements MethodMetricsRegister {

    /**
     * �o�^�����p�̃I�u�W�F�N�g�̏��������s���D�v���O�C���͎��g�������Ƃ��ė^���Ȃ���΂Ȃ�Ȃ��D
     * 
     * @param plugin ���������s���v���O�C���̃C���X�^���X
     */
    public DefaultMethodMetricsRegister(final AbstractPlugin plugin) {

        if (null == plugin) {
            throw new NullPointerException();
        }
        PluginInfo pluginInfo = plugin.getPluginInfo();
        if (METRIC_TYPE.METHOD_METRIC != pluginInfo.getMetricsType()) {
            throw new IllegalArgumentException("plugin must be method type!");
        }

        this.plugin = plugin;
    }

    /**
     * �������̃��\�b�h�̃��g���N�X�l�i�������j��o�^����
     */
    public void registMetric(final MethodInfo methodInfo, final int value)
            throws MetricAlreadyRegisteredException {
        MethodMetricsInfoManager manager = MethodMetricsInfoManager.getInstance();
        manager.putMetric(methodInfo, this.plugin, value);
    }

    /**
     * �v���O�C���I�u�W�F�N�g��ۑ����Ă������߂̕ϐ�
     */
    private final AbstractPlugin plugin;
}