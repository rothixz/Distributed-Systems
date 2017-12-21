package auxiliary.memFIFO;



public abstract class MemObject {
    /**
   *  Tamanho da memória em número de posições
   *
   *     nMax
   */

   protected int nMax = 0;

  /**
   *  Área de armazenamento
   *
   *    mem
   */

   protected Object [] mem = null;

  /**
   *  Instanciação da memória.
   *
   *    @param nElem tamanho da memória (n. de elementos do array de armazenamento)
   */

   protected MemObject (int nElem)
   {
     mem = new Object [nElem];
     nMax = nElem;
   }

  /**
   *  Escrita de um valor (método virtual).
   *
   *    @param val valor a armazenar
   */

   protected abstract void write (Object val);

  /**
   *  Leitura de um valor (método virtual).
   *
   *    @return valor armazenado
   */

   protected abstract Object read ();
    
}
