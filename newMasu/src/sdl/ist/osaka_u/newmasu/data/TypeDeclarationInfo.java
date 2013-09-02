package sdl.ist.osaka_u.newmasu.data;import java.util.HashSet;import java.util.List;import java.util.Set;import org.eclipse.jdt.core.dom.ASTNode;import org.eclipse.jdt.core.dom.IMethodBinding;import org.eclipse.jdt.core.dom.ITypeBinding;import org.eclipse.jdt.core.dom.IVariableBinding;import org.eclipse.jdt.core.dom.TypeDeclaration;import sdl.ist.osaka_u.newmasu.util.NodeFinder;public class TypeDeclarationInfo implements ClassInfo {	private final TypeDeclaration td;	private final ITypeBinding itb;	public TypeDeclarationInfo(TypeDeclaration td)			throws IllegalArgumentException {		if (td == null) {			throw new IllegalArgumentException();		}		this.td = td;		this.itb = td.resolveBinding();	}	public TypeDeclarationInfo(TypeDeclaration td, ITypeBinding itb)			throws IllegalArgumentException {		if (td == null || itb == null) {			throw new IllegalArgumentException();		}		this.td = td;		this.itb = itb;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public Set<VariableInfo> getFields() {		Set<VariableInfo> set = new HashSet<VariableInfo>();		for (IVariableBinding ivb : itb.getDeclaredFields()) {			set.add((VariableInfo) BindingManager.getDeclarationUnit(ivb));		}		return set;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public Set<MethodInfo> getMethods() {		Set<MethodInfo> set = new HashSet<MethodInfo>();		for (IMethodBinding imb : itb.getDeclaredMethods()) {			set.add((MethodInfo) BindingManager.getDeclarationUnit(imb));		}		return set;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public Set<ClassInfo> getInnerClass() {		Set<ClassInfo> set = new HashSet<ClassInfo>();		set = innerClasses(td, set);		return set;	}	private Set<ClassInfo> innerClasses(ASTNode node, Set<ClassInfo> infos) {		Set<ClassInfo> set = infos;		Object o = node.getProperty("Inner");		if (o == null) {			return set;		} else {			List<ITypeBinding> callers = (List<ITypeBinding>) o;			for (ITypeBinding itb : callers) {				ClassInfo info = (ClassInfo) BindingManager						.getDeclarationUnit(itb);				set.add(info);				set = innerClasses(info.getNode(), set);			}		}		return set;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public Set<ClassInfo> getSuperClass() {		Set<ClassInfo> set = new HashSet<ClassInfo>();		set = superClasses(this, set);		return set;	}	private Set<ClassInfo> superClasses(ClassInfo ci, Set<ClassInfo> infos) {		Set<ClassInfo> set = infos;		ITypeBinding itb = ci.resolveBinding();		ITypeBinding sup = itb.getSuperclass();		if (sup != null) {			ClassInfo superClassInfo = (ClassInfo) BindingManager					.getDeclarationUnit(sup);			set.add(superClassInfo);			set = superClasses(superClassInfo, set);		}		for (ITypeBinding inter : itb.getInterfaces()) {			ClassInfo inf = (ClassInfo) BindingManager					.getDeclarationUnit(inter);			if (inf != null) {				set.add(inf);				set = superClasses(inf, set);			}		}		return set;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public Set<ClassInfo> getSubClass() {		Set<ClassInfo> set = new HashSet<ClassInfo>();		set = subClasses(this, set);		return set;	}	private Set<ClassInfo> subClasses(ClassInfo ci, Set<ClassInfo> infos) {		Set<ClassInfo> set = infos;		ITypeBinding ib = ci.resolveBinding();		for (ClassInfo info : BindingManager.getExtendedClass(ib)) {			set.add(info);			set = subClasses(info, set);		}		return set;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public Set<ClassInfo> getUsedClass() {		Set<ClassInfo> set = new HashSet<ClassInfo>();		for (UnitInfo ui : BindingManager.getReferenceUnit(itb)) {			if (ui.getKind() == UnitInfo.METHOD) {				MethodInfo mi = (MethodInfo) ui;				set.add(NodeFinder.getClassInfo(mi.getNode()));			} else {				set.add((ClassInfo) ui);			}		}		return set;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public Set<ClassInfo> getUsingClass() {		Set<ClassInfo> set = new HashSet<ClassInfo>();		for (ITypeBinding binding : itb.getDeclaredTypes()) {			set.add((ClassInfo) BindingManager.getDeclarationUnit(binding));		}		return set;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public ITypeBinding resolveBinding() {		return itb;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public boolean isClass() {		return !td.isInterface();	}	@Override	public boolean isInterface() {		return td.isInterface();	}	@Override	public boolean isEnum() {		return false;	}	@Override	public boolean isAnonymousClass() {		return false;	}	// ////////////////////////////////////////////////////////////////////////////////////	@Override	public String getName() {		return itb.toString();	}	@Override	public ASTNode getNode() {		return td;	}	@Override	public int getKind() {		return CLASS;	}}