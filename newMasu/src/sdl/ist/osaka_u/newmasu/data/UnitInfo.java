package sdl.ist.osaka_u.newmasu.data;import org.eclipse.jdt.core.dom.ASTNode;public interface UnitInfo {	public static final int CLASS = 1;	public static final int METHOD = 2;	public static final int VARIABLE = 3;	public String getName();	public ASTNode getNode();	public int getKind();}