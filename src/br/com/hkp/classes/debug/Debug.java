/*
arquivo debug.java criado a partir de 22 de fevereiro de 2019
*/
package br.com.hkp.classes.debug;

import java.io.*;
import br.com.hkp.classes.stringtools.StringTools;

/**
 * Prove metodos static para redirecionar o objeto de saida System.err para um
 * arquivo texto. As saidas de err devem permitir rastrear a execucacao dos
 * metodos da classe. E tambem metodos para imprimir mensagens de depuracao
 * atraves de System.err, que podem estar redirecionadas para um arquivo ou nao.
 * 
 * @author Hugo Kaulino Pereira
 * @version 1.0
 * @since 1.0
 */
public class Debug  
{   
    private static final int TAB_LENGTH = 10;
    
    private static char htrace = '▬';
    private static char vtrace = '▌';
    private static String up = "▲\n";
    private static String down = "\n▼";
    private static String nivelStr = "NÍVEL ";
    
    private static FileOutputStream debugFile;
    private static final PrintStream ERR_COPY = System.err;
    private static boolean errRedirected = false;
    private static boolean debugging = false;
    private static boolean lastMsgWasPrintln = false;
    private static int level = 0;
    
    /**
     * Redireciona o objeto de saida System.err para um arquivo texto. Se o
     * arquivo nao existir sera criado, se existir sera sobrescrito.
     * <p>
     * Em caso de erro de IO e o arquivo nao puder ser criado, nenhuma excecao
     * eh lancada. Podendo resultar em execucao incorreta do programa que 
     * executar o metodo {@linkplain #debugOn() } ou 
     * {@link #setFileAndDebugOn(java.lang.String)  }
     * <p>
     * O metodo nao eh thread safe.
     * 
     * @param filename O nome de um arquivo texto. Pode conter o caminho.
     * 
     * @since 1.0
     */
    /*[01]----------------------------------------------------------------------
    *            Redireciona System.err para um arquivo de depuracao
    --------------------------------------------------------------------------*/
    public static void setDebugFile(String filename)
    { 
        /*
        Evita que System.err seja redirecionado para um arquivo Y se jah 
        estiver redirecionado para um arquivo X
        */
        if (errRedirected) return;
        
        try
        {
            debugFile = new FileOutputStream(filename);
            System.setErr(new PrintStream(debugFile));
            errRedirected = true;
        }
        catch(IOException e){}
        
    }//fim de setDebugFile()
    
    /**
     * Fecha o arquivo aberto por {@link #setDebugFile(java.lang.String) }
     * e restaura System.err
     * 
     * @since 1.0
     */
    /*[02]----------------------------------------------------------------------
    *                   Fecha o arquivo de depuracao
    --------------------------------------------------------------------------*/
    public static void closeDebugFile()
    {
        if (errRedirected)
        {
            try
            {
                debugFile.close();
                System.setErr(ERR_COPY);
                errRedirected = false;
            }
            catch(IOException e){}
        }
    }//fim de closeDebugFile()
    
     /**
    * Ativa mensagens de depuracao. Os metodos
    * {@link #debugPrint(java.lang.String) } e
    * {@link #debugPrintln(java.lang.String)} passam a imprimir as mensagens 
    * quando executados
    * 
    * @since 1.0
    */
    /*[03]----------------------------------------------------------------------
    *                   Ativa mensagens de depuracao 
    --------------------------------------------------------------------------*/
    public static void debugOn()
    {
        if (!debugging)
        {
            debugging = true;
            changeLevel(0, true);
        }
    }//fim de debugOn()
    
    /**
    *  Desativa mensagens de depuracao. Os metodos
    * {@link #debugPrint(java.lang.String) } e
    * {@link #debugPrintln(java.lang.String)} nao imprimem as mensagens 
    * quando executados
    * 
    * @since 1.0
    */
    /*[04]----------------------------------------------------------------------
    *                   Destiva mensagens de depuracao 
    --------------------------------------------------------------------------*/
    public static void debugOff()
    {
        if (debugging)
        {
            changeLevel(0, false);
            debugging = false;
        }
        
    }//fim de debugOff()
    
    /**
     * Os metodos {@link #debugPrint(java.lang.String) } e
     * {@link #debugPrintln(java.lang.String)} passam a enviar suas mensagens
     * para o arquivo filename com o redirecionamento do objeto System.err
     * 
     * @param filename O nome do arquivo para o qual System.err sera 
     * redirecionado
     * 
     * @since 1.0
     */
    /*[05]----------------------------------------------------------------------
    *         Ativa mensagens de depuracao e redireciona System.err
    --------------------------------------------------------------------------*/
    public static void setFileAndDebugOn(String filename)
    {
        setDebugFile(filename);
        debugOn();
    }//fim de setFileAndDebugOn()
    
    /**
     * Fecha o arquivo aberto para redirecionamento dos metodos 
     * {@link #debugPrint(java.lang.String) } e
     * {@link #debugPrintln(java.lang.String)} e restaura System.err
     * 
     * @since 1.0
     */
    /*[06]----------------------------------------------------------------------
    *         Desativa mensagens de depuracao e restaura System.err
    --------------------------------------------------------------------------*/
    public static void closeFileAndDebugOff()
    {
        debugOff();
        closeDebugFile();
    }//fim de closeFileAndDebugOff()
    
    /**
     * Uma string com texto de depuracao para ser exibido sem mudanca de linha.
     * Este metodo executa System.err.print(msg) se depuracao estiver ativada.
     * 
     * @param msg Um texto para depuracao de codigo.
     * 
     * @since 1.0
     */
    /*[07]----------------------------------------------------------------------
    *           Imprime mensagem de depuracao se depuracao estiver ativa
    --------------------------------------------------------------------------*/
    public static void debugPrint(String msg)
    {
        if (debugging)
        {
            if (lastMsgWasPrintln) System.err.print(tab());
            printString(msg);
            lastMsgWasPrintln = false;
        }
      
    }//fim de debugPrint()
    
     /**
     * Uma string com texto de depuracao para ser exibido com mudanca de linha.
     * Este metodo executa System.err.println(msg) se depuracao estiver ativada.
     * 
     * @param msg Um texto para depuracao de codigo.
     * 
     * @since 1.0
     */
    /*[08]----------------------------------------------------------------------
    *           Imprime mensagem de depuracao se depuracao estiver ativa
    --------------------------------------------------------------------------*/
    public static void debugPrintln(String msg)
    {
        if (debugging)
        {
            if (lastMsgWasPrintln) System.err.print(tab());
            printString(msg); System.err.println();
            lastMsgWasPrintln = true;
        }
    }//fim de debugPrintln()
      
    /**
     * Retorna true se os metodos {@link #debugPrint(java.lang.String) } e
     * {@link #debugPrintln(java.lang.String)} estao exibindo as mensagens de 
     * depuracao quando executados
     * 
     * @return true se depuracao ativada. false se nao.
     * 
     * @since 1.0
     */
    /*[09]----------------------------------------------------------------------
    *              Retorna se mensagens de depuracao estao ativadas
    --------------------------------------------------------------------------*/
    public static boolean isDebugging()
    {
        return debugging;
    }//fim de isDebugging()
    
    /**
     * Informa se System.err esta redirecionado para um arquivo
     * 
     * @return true se System.err estiver redirecionado. false se nao.
     * 
     * @since 1.0
     */
    /*[10]----------------------------------------------------------------------
    *          Retorna se System.err esta redirecionado para um arquivo
    --------------------------------------------------------------------------*/
    public static boolean isSystemErrRedirected()
    {
        return errRedirected;
    }//fim de isSystemErrRedirected()
    
    /**
     * Cada vez que este metodo eh executado acrescenta uma 
     * tabulacao antes de cada mensagem impressa pelos metodos 
     * {@link #debugPrint(java.lang.String) } e
     * {@link #debugPrintln(java.lang.String) }
     * <p>
     * Pode ser usado na depuracao de codigo de metodos recursivos, sendo 
     * chamado imediatamente antes de cada execucao recursiva de um metodo,
     * fazendo com que as mensagens exibidas por esta execucao recursiva
     * aparecam identadas em relacao as mensagens de depuracao exibidas pela 
     * execucao do metodo em um nivel de recursao imediatamente superior.
     * <p>
     * Nesse caso, no retorno da chamada recursiva, o metodo {@link #decTab() }
     * deve ser imediatamente executado para restaurar o nivel de identacao ao
     * que era antes da chamada da instancia recursiva.
     * <p>
     * O metodo soh tem efeito quando {@link #isDebugging() } estiver retornando
     * true. 
     * 
     * @since 1.0
     */
    /*[11]----------------------------------------------------------------------
    *      Incrementa em um nivel de tabulacao as mensagens impressas.
    --------------------------------------------------------------------------*/
    public static void incTab()
    {
        changeLevel(1, false);
    }//fim de incTab()
    
    /**
     * Recua o nivel de identacao das mensagens de depuracao em um caractere de
     * tabulacao. Se o nivel ja estiver em 0 ( sem tabulacao ) entao nada faz.
     * <p>
     * O metodo soh tem efeito quando {@link #isDebugging() } estiver retornando
     * true. 
     * 
     * @since 1.0
     */
    /*[12]----------------------------------------------------------------------
    *      Decrementa em um nivel de tabulacao as mensagens impressas.
    --------------------------------------------------------------------------*/
    public static void decTab()
    {
        if (level > 0) changeLevel(-1, false);
    }//fim de decTab()
    
    /**
     * Depois de executar este metodo serao utilizados apenas caracteres ascii
     * para formatar os delimitadores de nivel de identacao.
     * 
     * @since 1.0
     */
    /*[13]----------------------------------------------------------------------
    *     Passa a usar apenas caracteres ascii no delimitador de nivel
    --------------------------------------------------------------------------*/
    public static void setToAscii()
    {
        htrace = '-';
        vtrace = '|';
        up = "|\n";
        down = "\n|";
        nivelStr = "NIVEL ";
    }//fim de setToAscii()
        
    /*[14]----------------------------------------------------------------------
    *           Tabula as mensagens de acordo com o campo level.
    --------------------------------------------------------------------------*/
    private static String tab()
    {
        return StringTools.repeat
                           (
                               vtrace + StringTools.repeat(' ', TAB_LENGTH - 1),
                               level
                           );
    }//fim de tab()
    
    /*[15]----------------------------------------------------------------------
    *            Imprime uma string com uma linha delimitadora.
    --------------------------------------------------------------------------*/
    private static String delimiter(int level)
    {
        return StringTools.repeat(htrace, level * TAB_LENGTH + TAB_LENGTH);
    }//fim de printDelimiter()
   
    /*[16]----------------------------------------------------------------------
    *    Insere tabulacoes na msg apos cada caractere de new line de acordo
    *    com o valor do campo level. Isto eh, tabula cada string que vai ser
    *    impressa em uma nova linha.
    --------------------------------------------------------------------------*/
    private static void printString(String s)
    {
                
        int beginSubstring = 0;
        int sLength = s.length();
        
        for (int i = 0; i < sLength; i++)
        {
            if (s.charAt(i) == '\n')
            {
                System.err.println(s.substring(beginSubstring, i));
                System.err.print(tab());
                beginSubstring = i + 1;
            }
            else if (i == sLength - 1)
                System.err.print(s.substring(beginSubstring,i + 1));
        }
      
    }//fim de printString()
   
    /*[17]----------------------------------------------------------------------
    *         Incrementa ou decrementa o nível de identacao das mensagens.
    --------------------------------------------------------------------------*/
    private static void changeLevel(int i, boolean isOff)
    {
        if (debugging)
        {
            boolean change = (i*i == 1);
            
            debugPrintln("");
            if (change || (!isOff)) debugPrintln(up + nivelStr + level);
            System.err.println(delimiter(Math.max(level, level + i))); 
            level += i;
            if (change || isOff) debugPrintln(nivelStr + level + down);
            debugPrintln("");
        }
    }//fim de changeLevel()
      
    /**
     * Um metodo demonstrando usos da classe.
     * 
     * @param args Nao utilizado.
     */
    public static void main(String[] args)
    {
        System.out.println(new Debug().getClass().getResource(""));
    }//fim de main()
   
}//fim da classe Debug