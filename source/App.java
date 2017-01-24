import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;

// класс App наследует класс JFrame
public class App extends JFrame {
    //класс холста
    static GLCanvas canvas = null;
    //класс аниматор, обеспечиает анимацию на холсте. Отпадает необходимость в репеинтах
    static Animator animator = null;
    public static void main(String[] args) {
       //экземпляр класса окна
        App app = new App();

        //метод для синхронизации элементов (не обязателен, хорошая практика)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //отображаем окно программы
                app.setVisible(true);
                //фокусируемся на холсте, чтобы холст сразу реагировал на нажатие
                canvas.requestFocusInWindow();
                // стартуем аниматор
                animator.start();
            }
        });
    }

    App(){
        super("Lab3_OpenGL - вращение фигуры в пространстве");
        //закрытие приложения при нажатии на крестик
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //размер окна
        setSize(800,800);
        //// Волшебное заклинание (пока что для меня) для инициализации JOGL
        //Создаются  классы для загрузки необходимой версии OpenGL (использую 2.0)
        //и инициализации холста
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        //создаём экземпляр дисплея
        DisplayApp display = new DisplayApp();
        /////
        //добавляем холсту прослушивание графики и клавиатуры с дисплея
        canvas.addGLEventListener(display);
        canvas.addKeyListener(display);
        //инициализация аниматора
        animator = new Animator(canvas);
        //добавляем холст в окно
        add(canvas);
        //центрируем окно
        centerWindow(this);
    }
    //метод для центрирования окна
    private void centerWindow(Component frame){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();

        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        frame.setLocation((screenSize.width-frameSize.width)>> 1, (screenSize.height-frameSize.height) >>1);
    }
}
