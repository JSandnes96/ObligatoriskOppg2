////////////////// class DobbeltLenketListe //////////////////////////////


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;



public class DobbeltLenketListe<T> implements Liste<T> {

    /**
     * Node class
     * @param <T>
     */
    private static final class Node<T> {
        private T verdi;                   // nodens verdi
        private Node<T> forrige, neste;    // pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }
    }

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;         // antall endringer i listen



    public DobbeltLenketListe() {
        //throw new NotImplementedException();
    }

    public DobbeltLenketListe(T[] a) {
        this();

        if (a == null){
            throw new NullPointerException("Tabellen a er null");
        }

        int i = 0; for (; i < a.length && a[i] == null; i++);

        if (i < a.length)
        {
            Node<T> p = hode = new Node<T>(a[i], null, null);  // den første noden
            antall = 1;                                 // vi har minst en node

            for (i++; i < a.length; i++)
            {
                if (a[i] != null)
                {
                    p = p.neste = new Node<T>(a[i], null, null);   // en ny node
                    antall++;
                }
            }
            hale = p;
        }




    }

    public Liste<T> subliste(int fra, int til){
        throw new NotImplementedException();
    }

    @Override
    public int antall() {
        return antall;
    }

    @Override
    public boolean tom() {
        return antall == 0;
    }

    @Override
    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi, "Du kan ikke ha null-verdier");
        if (antall == 0){
            hode = hale = new Node<T>(verdi, null, null); //Ved tom liste
        }
        else{
            hale = hale.neste = new Node<T>(verdi, hale, null); //ellers legges den bakerst
            endringer++;
        }

        antall++; //antallet noder i listen øker
        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi) {

        Objects.requireNonNull(verdi, "Du kan ikke ha null-verdier");

        indeksKontroll(indeks, true);

        if (indeks == 0)                     // ny verdi skal ligge først
        {
            hode = new Node<T>(verdi, null, hode);    // legges først
            if (antall == 0) hale = hode;      // hode og hale går til samme node
        }
        else if (indeks == antall)           // ny verdi skal ligge bakerst
        {
            hale = hale.neste = new Node<T>(verdi, hale.forrige, hale.neste);  // legges bakerst
        }
        else
        {
            Node<T> p = hode;                  // p flyttes indeks - 1 ganger
            for (int i = 1; i < indeks; i++) p = p.neste;

            p.neste = new Node<T>(verdi, p.forrige, p.neste);  // verdi settes inn i listen
        }

        antall++;

    }

    @Override
    public boolean inneholder(T verdi) {
        return indeksTil(verdi) != -1;
    }

    private Node<T> finnNode(int indeks){
        Node<T> p = hode;

        for(int i = 0; i < indeks; i++){
            p = p.neste;
        }

        return p;
    }


    @Override
    public T hent(int indeks) {
        indeksKontroll(indeks, false);  // Se Liste, false: indeks = antall er ulovlig
        return finnNode(indeks).verdi;
    }

    @Override
    public int indeksTil(T verdi) {

        if(verdi == null){
            return -1;
        }

        Node<T> p = hode;

        for(int i = 0; i < antall; i++){
            if(p.verdi.equals(verdi)){
                return i;
            }
            p = p.neste; //Kanskje denne må flyttes
        }
        return -1;

    }

    @Override
    public T oppdater(int indeks, T nyverdi) {
        Objects.requireNonNull(nyverdi, "Ikke tillatt med null-verdier!");

        indeksKontroll(indeks, false);  // Se Liste, false: indeks = antall er ulovlig

        Node<T> p = finnNode(indeks);
        T gammelVerdi = p.verdi;

        p.verdi = nyverdi;
        return gammelVerdi;
    }

    @Override
    public boolean fjern(T verdi) {
        if (verdi == null){ //ingen nullverdier i listen
            return false;
        }

        Node<T> q = hode; //hjelpepeker
        Node<T> p = hale; //hjelpepeker

        while (q != null){              //q skal finne verdien t
            if (q.verdi.equals(verdi)) {
                break;                  //verdien er funnet
            }
            p = q;
            q = q.neste;
        }


        if(q == null){
            return false;  //fant ikke verdi
        }

        else if(q == hode){
            hode = hode.neste;  //går forbi q
        }

        else{
            p.neste = q.neste; //går forbi q
        }


        if(q == hale){
            hale = p;  //oppdaterer hale
        }


        q.verdi = null; //nuller verdien til q
        q.neste = null; //nuller nestepeker

        antall--; //en node mindre i listen

        return true; //vellykket fjerning


    }

    @Override
    public T fjern(int indeks) {
        //KOPIERT DIREKTE:

        indeksKontroll(indeks, false);  // Se Liste, false: indeks = antall er ulovlig

        T temp;                              // hjelpevariabel

        if (indeks == 0)                     // skal første verdi fjernes?
        {
            temp = hode.verdi;                 // tar vare på verdien som skal fjernes
            hode = hode.neste;                 // hode flyttes til neste node
            if (antall == 1) hale = null;      // det var kun en verdi i listen
        }
        else
        {
            Node<T> p = finnNode(indeks - 1);  // p er noden foran den som skal fjernes
            Node<T> q = p.neste;               // q skal fjernes
            temp = q.verdi;                    // tar vare på verdien som skal fjernes

            if (q == hale) hale = p;           // q er siste node
            p.neste = q.neste;                 // "hopper over" q
        }

        antall--;                            // reduserer antallet
        return temp;                         // returner fjernet verdi

    }

    @Override
    public void nullstill() {
        //1
        /*Node<T> p = hode;
        Node<T> q = null;

        while (p != null){
            q = p.neste;
            p.neste = null;
            p.verdi = null;
            p = q;
        }

        hode = hale = null;
        antall = 0;*/


        //2

        for (int i = 0; i < antall; i++){
            fjern(i);
        }


    }

    @Override
    public String toString() {

        Node<T> p = hode;

        StringJoiner s = new StringJoiner(", ", "[", "]");

        if(!tom()){
            while(p != null){
                s.add(p.verdi.toString());
                p = p.neste;
            }
        }

        return s.toString();

        /*StringBuilder s = new StringBuilder();

        s.append('[');

        if (!tom())
        {
            Node<T> p = hode;
            s.append(p.verdi);

            p = p.neste;

            while (p != null)  // tar med resten hvis det er noe mer
            {
                s.append(',').append(' ').append(p.verdi);
                p = p.neste;
            }
        }

        s.append(']');

        return s.toString();*/
    }

    public String omvendtString() {

        Node<T> p = hale;

        StringJoiner sj = new StringJoiner(", ", "[", "]");

        if(!tom()){
            while(p != null){
                sj.add(p.verdi.toString());
                p = p.forrige;
            }
        }

        return sj.toString();
    }

    @Override
    public Iterator<T> iterator() {
        throw new NotImplementedException();
    }

    public Iterator<T> iterator(int indeks) {
        throw new NotImplementedException();
    }

    private class DobbeltLenketListeIterator implements Iterator<T>
    {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator(){
            throw new NotImplementedException();
        }

        private DobbeltLenketListeIterator(int indeks){
            throw new NotImplementedException();
        }

        @Override
        public boolean hasNext(){
            throw new NotImplementedException();
        }

        @Override
        public T next(){
            throw new NotImplementedException();
        }

        @Override
        public void remove(){
            throw new NotImplementedException();
        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
        throw new NotImplementedException();
    }

} // class DobbeltLenketListe


