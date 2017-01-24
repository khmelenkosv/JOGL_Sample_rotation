import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import java.awt.*;
import java.awt.event.*;

//реализуем интерфейс холста JOGL и добавляем прослушку клавиш
public class DisplayApp implements GLEventListener, KeyListener {

    //Кватернионы используем для предотвращения эффекта "шарнирного замка"
    //при комплексном вращении фигуры по нескольким осям
    Quaternion quaternion = new Quaternion();
    Quaternion xQuat = new Quaternion();
    Quaternion yQuat = new Quaternion();
    Quaternion zQuat = new Quaternion();

    // оси направлений
    float xAxis[] = {1.f, 0.f, 0.f};
    float yAxis[] = {0.f, 1.f, 0.f};
    float zAxis[] = {0.f, 0.f, 1.f};

    // углы поворота для каждой оси
    float angleX = 0.f, angleY = 0.f, angleZ = 0.f;

    // класс для отображения текста на экране
    TextRenderer renderer;

    //первичная инициализация картинки
    //В методах init и display должны быть созданы свои GL и GLU, если используются (реализация JOGL)
    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        GLU glu = new GLU();
        //Используем перспективную проекцию с контролем размеров экрана
        gl.glMatrixMode(GL2.GL_PROJECTION);
        double w = ((Component) glAutoDrawable).getWidth();
        double h = ((Component) glAutoDrawable).getHeight();
        double aspect = w / h;
        glu.gluPerspective(80.0f, aspect, 2f, 20.0f);
        //инициализация текстовой строки
        renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 15));
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {

        GL2 gl = glAutoDrawable.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();

        //Устанавливаем текущей матрицу обзора
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        //цвет фона чёрный
        gl.glClearColor(.0f, .0f, .0f, 1.f);

        //смотрим на объект справа(инвертировано) сверху вниз
        glu.gluLookAt(-5.f, 4.f, 0.f,
                      0.f, 0.f, 5.f,
                      0.f, 1.f, 0.f);

        //очистка буфера глубины и цвета
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        //сглаживание
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_BLEND);
        //такая комбинация даёт эффект прозрачности, оставил в таком виде
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);

        //сглаживание точек, линий, многоугольников
        gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glHint(GL2.GL_POLYGON_SMOOTH, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_POLYGON_SMOOTH);

        //вкючаем проверку приоритета отрисовки
        gl.glEnable(GL2.GL_DEPTH_TEST);
        //включаем режим рисования только лицевой стороны полигонов
        gl.glEnable(GL2.GL_CULL_FACE);

        //перенос начала координат
        gl.glTranslated(0, 0.5f, 6);

        //вспомогательная матрица
        float Matrix[] = new float[16];

        //аналог glRotate но с использованием кватернионов (присваиваем вектору направления и
        // скаляру (элементам кватерниона) соответствующих значений переноса)
        // метод получает радианы - переводим в градусы
        xQuat.setFromAngleNormalAxis((float) ((angleX / 180) * Math.PI), xAxis);
        yQuat.setFromAngleNormalAxis((float) ((angleY / 180) * Math.PI), yAxis);
        zQuat.setFromAngleNormalAxis((float) ((angleZ / 180) * Math.PI), zAxis);
        //перемножаем кватернионы
        quaternion.mult(xQuat).mult(yQuat).mult(zQuat);
        //создание гомогенной матрицы
        quaternion.toMatrix(Matrix, 0);
        //умножаем текущую матрицу на получившуюся
        gl.glMultMatrixf(Matrix, 0);

        //рисуем оси
        gl.glBegin(GL2.GL_LINES);

        gl.glColor3f(1, 1, 1);
        gl.glVertex3f(-100f, 0, 0);
        gl.glVertex3f(100, 0, 0);

        gl.glColor3f(1, 0, 1);
        gl.glVertex3f(0, 100, 0);
        gl.glVertex3f(0, -100, 0);

        gl.glColor3f(1, 1, 0);
        gl.glVertex3f(0, 0, 100);
        gl.glVertex3f(0, 0, -100);
        gl.glEnd();

        //кубик
        gl.glColor3f(0.75f,0.32f,0.13f);
        glut.glutWireCube(2);

        //стороны пирамиды
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glColor3f(1, 0, 0);
        gl.glVertex3f(0, 1, 0);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(-1, -1, -1);

        gl.glColor3f(0, 1, 0);
        gl.glVertex3f(0, 1, 0);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1, -1, 1);

        gl.glColor3f(0, 0, 1);
        gl.glVertex3f(0, 1, 0);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(1, -1, -1);

        gl.glColor3f(1, 0.5f, 0.7f);
        gl.glVertex3f(0, 1, 0);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(1, -1, 1);
        gl.glEnd();

        gl.glColor3f(0.5f, 1, 0.5f);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(1, -1, 1);
        gl.glEnd();

        //возвращаемся в точку обзора
        gl.glTranslated(0, -0.5f, -6);

        //отключаем сглаживание, тест глубины, режим отрисовки полигонов
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glDisable(GL2.GL_BLEND);
        gl.glDisable(GL2.GL_POINT_SMOOTH);
        gl.glDisable(GL2.GL_LINE_SMOOTH);
        gl.glDisable(GL2.GL_POLYGON_SMOOTH);
        gl.glDisable(GL.GL_DEPTH_TEST);

        //рисуем текст с подсказками
        //значения отражаются в радианах, на установленное значение будет производится
        //поворот вокруг соответствующей оси за 1 такт
        renderer.beginRendering(((Component) glAutoDrawable).getWidth(),((Component) glAutoDrawable).getWidth());
        renderer.setSmoothing(true);
        renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        renderer.draw("Ось X "+String.valueOf(angleX) + "(W,S)", 10, 40);
        renderer.setColor(1.0f, 0.0f, 1.0f, 1.0f);
        renderer.draw("Ось Y "+String.valueOf(angleY) + "(A,D)", 10, 25);
        renderer.setColor(1.0f, 1.0f, 0.0f, 1.0f);
        renderer.draw("Ось Z "+String.valueOf(angleZ) + "(Q,E)", 10, 10);
        renderer.endRendering();
    }

    //нажатие клавиш задаёт углы изменения соответствующих осей в радианах
    //при добавлении или уменьшении менятся скорость поворота фигуры
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            angleX++;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            angleX--;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            angleY++;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            angleY--;
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            angleZ--;
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            angleZ++;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
