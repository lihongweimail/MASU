package sdl.ist.osaka_u.newmasu.gomi.PDG;

import org.eclipse.jdt.core.dom.*;

public enum PDGSampleProcessor {

    IF(IfStatement.class){
        @Override
        public void process(ASTNode node){
           vis.visit((IfStatement)node);
        }
    },
    FOR(ForStatement.class){
        @Override
        public void process(ASTNode node){
            vis.visit((ForStatement)node);
        }
    },
    BLOCK(Block.class){
        @Override
        public void process(ASTNode node){
            vis.visit((Block)node);
        }
    },
    METHOD_DECLARATION(MethodDeclaration.class){
        @Override
        public void process(ASTNode node){
            vis.visit((MethodDeclaration)node);
        }
    },

    _DEF_(Object.class){
        @Override
        public void process(ASTNode node){
            vis.def(node);
        }
    }
    ;

    private static String indent = "  ";
    private static PDGSample vis = null;

    private final Class<?> clazz;
    private PDGSampleProcessor(Class<?> clazz) {
        this.clazz = clazz;
    }

    public abstract void process(ASTNode node);

    public static PDGSampleProcessor get(Object obj, PDGSample vi) {
        vis = vi;
        for (PDGSampleProcessor processor : values()) {
            if(processor.clazz.isAssignableFrom(obj.getClass())) {
                return processor;
            }
        }
//        System.out.println("default");
//        throw new RuntimeException();
        return _DEF_;
    }
}
