package sdl.ist.osaka_u.newmasu.data;import org.eclipse.jdt.core.dom.ASTNode;import org.eclipse.jdt.core.dom.EnumConstantDeclaration;import org.eclipse.jdt.core.dom.IVariableBinding;public class EnumConstDeclarationInfo implements VariableInfo {	private final EnumConstantDeclaration ecd;	private final IVariableBinding ivb;	public EnumConstDeclarationInfo(EnumConstantDeclaration ecd)			throws IllegalArgumentException {		if (ecd == null) {			throw new IllegalArgumentException();		}		this.ecd = ecd;		this.ivb = ecd.resolveVariable();	}	public EnumConstDeclarationInfo(EnumConstantDeclaration ecd,			IVariableBinding ivb) throws IllegalArgumentException {		if (ecd == null || ivb == null) {			throw new IllegalArgumentException();		}		this.ecd = ecd;		this.ivb = ivb;	}	@Override	public boolean isDeclaration() {		return true;	}	@Override	public boolean isField() {		return false;	}	@Override	public boolean isParameter() {		return false;	}	@Override	public IVariableBinding resolveBinding() {		return ivb;	}	@Override	public String getName() {		return ivb.toString();	}	@Override	public ASTNode getNode() {		return ecd;	}	@Override	public int getKind() {		return VARIABLE;	}}