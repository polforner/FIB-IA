package src.blablacar;


import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

import IA.Comparticion.*;
import aima.basic.XYLocation;


/**
 * @author Alex H.
 */



public class BlaBlaEstado {

	private ArrayList<ArrayList<Integer>> trayectos;
	private ArrayList<Integer> id_cond;
	private ArrayList<Integer> distancias;
	private static Usuarios cjt_usuarios;
	private int num_cond;


	/**
	 * @param users instancia de la clase Usuarios sobre la que estarán todos los participantes del problema
	 * @param modo con modo = 1 se genera una solución con el método aleatoria, con modo = 0 se genera la solución de manera greedy
	 * @return Una instancia de la clase BlaBlaEstado en la que ya se ha generado una solución inicial.
	 */
	public BlaBlaEstado(Usuarios users, int modo) {

		cjt_usuarios = users;

		ArrayList<Integer> id_conductores = new ArrayList<Integer> ();
		ArrayList<Integer> id_pasajeros = new ArrayList<Integer> ();

		num_cond = 0;

		distancias = new ArrayList<Integer> ();
		trayectos = new ArrayList<ArrayList<Integer>> ();

		id_cond = id_conductores;


		for (int i = 0; i < users.size(); ++i) {

			if (users.get(i).isConductor()) {

				id_conductores.add(i+1);
				distancias.add(0);
				trayectos.add(new ArrayList<Integer>());
				trayectos.get(num_cond).add(i+1);
				++num_cond;

			}

			else id_pasajeros.add(i+1);

		}


		if (modo == 1) random_sol(id_conductores, id_pasajeros);
		else greedy_sol(id_conductores, id_pasajeros);

	}

	/**
	 * @param state Instancia de una clase BlaBlaEstado
	 * @return una constructora por copia
	 */
	public BlaBlaEstado(BlaBlaEstado state) {
		this.trayectos = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Integer> tray: state.trayectos) {
			ArrayList<Integer> current = new ArrayList<Integer>();
			for (Integer us: tray) {
				current.add(us);
			}
			this.trayectos.add(current);
		}

		this.id_cond = new ArrayList<Integer>();
		for (Integer cond: state.id_cond) {
			this.id_cond.add(cond);
		}

		this.distancias = new ArrayList<Integer>();
		for (Integer dist: state.distancias) {
			this.distancias.add(dist);
		}

		this.num_cond = state.num_cond;
	}


	/**
	 * @param car identificador del coche el cuál quiere referirse (refiriéndose a él como su ordinal)
	 * @return la longitud de la ruta en concreto
	 */
	public int get_route_length(int car) {
		return trayectos.get(car).size();
	}

	private void random_sol(ArrayList<Integer> id_conductores, ArrayList<Integer> id_pasajeros) {


		boolean solucion_valida = false;

		int max_dist = 300;


		while (!solucion_valida) {

			ArrayList<ArrayList<Integer>> pasajeros = new ArrayList<ArrayList<Integer>> ();

			for (int i = 0; i < distancias.size(); ++i) {

				distancias.set(i,0);
				trayectos.get(i).clear();
				trayectos.get(i).add(id_conductores.get(i));

				pasajeros.add(new ArrayList<Integer>());

			}

			int num_no_pillados = id_pasajeros.size();

			Random r = new Random();

			ArrayList<Boolean> vb = new ArrayList<Boolean> (id_pasajeros.size());

			for (int i = 0; i < id_pasajeros.size(); ++i) vb.add(false);


			while (num_no_pillados > 0) {

				for (int i = 0; i < id_conductores.size() && distancias.get(i) <= max_dist && num_no_pillados > 0; ++i) {

					int aux = trayectos.get(i).size();
					int id_1 = trayectos.get(i).get(aux-1);

					int psj_1 = pasajeros.get(i).size() == 0 ? 0 : pasajeros.get(i).get(0);
					int psj_2 = pasajeros.get(i).size() <= 1 ? 0 : pasajeros.get(i).get(1);

					int x_act = id_1 > 0 ? cjt_usuarios.get(id_1 - 1).getCoordOrigenX() : cjt_usuarios.get(-(id_1 + 1)).getCoordDestinoX();
					int y_act = id_1 > 0 ? cjt_usuarios.get(id_1 - 1).getCoordOrigenY() : cjt_usuarios.get(-(id_1 + 1)).getCoordDestinoY();

					int num_pasajeros = pasajeros.get(i).size();


					boolean recoger = num_pasajeros == 0 || (num_pasajeros == 1 && r.nextInt(2) == 0);


					if (recoger) {

						--num_no_pillados;
						int num_recoger = r.nextInt(id_pasajeros.size());

						while (vb.get(num_recoger)) num_recoger = r.nextInt(id_pasajeros.size());

						vb.set(num_recoger, true);

						pasajeros.get(i).add(id_pasajeros.get(num_recoger));

						int x_dest = cjt_usuarios.get(id_pasajeros.get(num_recoger)-1).getCoordOrigenX();
						int y_dest = cjt_usuarios.get(id_pasajeros.get(num_recoger)-1).getCoordOrigenY();

						distancias.set(i, distancias.get(i) + Math.abs(x_act - x_dest) + Math.abs(y_act - y_dest));

						trayectos.get(i).add(id_pasajeros.get(num_recoger));


					}

					else {

						int xd = r.nextInt(pasajeros.get(i).size());
						int id_dejar = pasajeros.get(i).get(xd);

						int x_dest = cjt_usuarios.get(id_dejar-1).getCoordDestinoX();
						int y_dest = cjt_usuarios.get(id_dejar-1).getCoordDestinoY();

						pasajeros.get(i).remove(xd);

						distancias.set(i, distancias.get(i) + Math.abs(x_act - x_dest) + Math.abs(y_act - y_dest));

						trayectos.get(i).add(-id_dejar);

						

					}


				}

			}

			
			
			for (int i = 0; i < distancias.size(); ++i) {

				int psj_1 = pasajeros.get(i).size() == 0 ? 0 : pasajeros.get(i).get(0);
				int psj_2 = pasajeros.get(i).size() <= 1 ? 0 : pasajeros.get(i).get(1);


				if (psj_1 != 0 && psj_2 != 0) {

					int primero = r.nextInt(2) == 0 ? psj_1 : psj_2;
					int segundo = primero == psj_1 ? psj_2 : psj_2;
				    
					int id_u = trayectos.get(i).get(trayectos.get(i).size() - 1);
				    int x_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoX() : cjt_usuarios.get(id_u-1).getCoordOrigenX();
				    int y_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoY() : cjt_usuarios.get(id_u-1).getCoordOrigenY();

					trayectos.get(i).add(-primero);
					trayectos.get(i).add(-segundo);

					Usuario u_prim = cjt_usuarios.get(primero-1);
					Usuario u_segu = cjt_usuarios.get(segundo-1);

					distancias.set(i, distancias.get(i) + Math.abs(x_a - u_prim.getCoordDestinoX()) + Math.abs(y_a - u_prim.getCoordDestinoY()));
					distancias.set(i, distancias.get(i) + Math.abs(u_prim.getCoordDestinoX() - u_segu.getCoordDestinoX()) + Math.abs(u_prim.getCoordDestinoY() - u_segu.getCoordDestinoY()));
				    
					Usuario conductor = cjt_usuarios.get(id_conductores.get(i)-1);
				    int x_f = conductor.getCoordDestinoX();
				    int y_f = conductor.getCoordDestinoY();
				    distancias.set(i, distancias.get(i) + Math.abs(u_segu.getCoordDestinoX() - x_f) + Math.abs(u_segu.getCoordDestinoY() - y_f));
				    trayectos.get(i).add(-id_conductores.get(i));

				}

				else if (psj_1 != 0) {

					int id_u = trayectos.get(i).get(trayectos.get(i).size() - 1);
				    int x_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoX() : cjt_usuarios.get(id_u-1).getCoordOrigenX();
				    int y_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoY() : cjt_usuarios.get(id_u-1).getCoordOrigenY();

					trayectos.get(i).add(-psj_1);

					Usuario conductor = cjt_usuarios.get(id_conductores.get(i)-1);
				    int x_f = conductor.getCoordDestinoX();
				    int y_f = conductor.getCoordDestinoY();

					if (id_u > 0) {

						distancias.set(i, distancias.get(i) + Math.abs(x_a - cjt_usuarios.get(id_u-1).getCoordDestinoX()) + Math.abs(y_a - cjt_usuarios.get(id_u-1).getCoordDestinoY()));

						x_a = cjt_usuarios.get(id_u-1).getCoordDestinoX();
						y_a = cjt_usuarios.get(id_u-1).getCoordDestinoY();
					}


				    distancias.set(i, distancias.get(i) + Math.abs(x_a - x_f) + Math.abs(y_a - y_f));
				    trayectos.get(i).add(-id_conductores.get(i));

				}

				else if (psj_2 != 0) {


					int id_u = trayectos.get(i).get(trayectos.get(i).size() - 1);
				    int x_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoX() : cjt_usuarios.get(id_u-1).getCoordOrigenX();
				    int y_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoY() : cjt_usuarios.get(id_u-1).getCoordOrigenY();

					trayectos.get(i).add(-psj_2);

					Usuario conductor = cjt_usuarios.get(id_conductores.get(i)-1);
				    int x_f = conductor.getCoordDestinoX();
				    int y_f = conductor.getCoordDestinoY();

					if (id_u > 0) {

						distancias.set(i, distancias.get(i) + Math.abs(x_a - cjt_usuarios.get(id_u-1).getCoordDestinoX()) + Math.abs(y_a - cjt_usuarios.get(id_u-1).getCoordDestinoY()));

						x_a = cjt_usuarios.get(id_u-1).getCoordDestinoX();
						y_a = cjt_usuarios.get(id_u-1).getCoordDestinoY();
					}
				    distancias.set(i, distancias.get(i) + Math.abs(x_a - x_f) + Math.abs(y_a - y_f));
				    trayectos.get(i).add(-id_conductores.get(i));

				}

				else {


					int id_u = trayectos.get(i).get(trayectos.get(i).size() - 1);
					Usuario conductor = cjt_usuarios.get(id_conductores.get(i)-1);
				    int x_f = conductor.getCoordDestinoX();
				    int y_f = conductor.getCoordDestinoY();
				    int x_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoX() : cjt_usuarios.get(id_u-1).getCoordOrigenX();
				    int y_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoY() : cjt_usuarios.get(id_u-1).getCoordOrigenY();
				    distancias.set(i, distancias.get(i) + Math.abs(x_a - x_f) + Math.abs(y_a - y_f));
				    trayectos.get(i).add(-id_conductores.get(i));
				}



				if (distancias.get(i) > max_dist) {
					solucion_valida = false;
					break;
				}

				else solucion_valida = true;

			}

			

		}

	}

	/**
	 * @return escribe la ruta de cada coche por pantalla
	 */
	public void escribir_ruta() {


		for (int i = 0; i < distancias.size(); ++i) {

			System.out.println("El coche " + id_cond.get(i) + " ha recorrido " + distancias.get(i) + " y ha hecho esto: ");

			for (Integer x : trayectos.get(i)) System.out.print(x + " , ");
			System.out.println("");

		}

	}

	private void greedy_sol(ArrayList<Integer> id_conductores, ArrayList<Integer> id_pasajeros) {



		int max_dist = 300;


			ArrayList<ArrayList<Integer>> pasajeros = new ArrayList<ArrayList<Integer>> ();

			for (int i = 0; i < distancias.size(); ++i) {

				distancias.set(i,0);
				trayectos.get(i).clear();
				trayectos.get(i).add(id_conductores.get(i));

				pasajeros.add(new ArrayList<Integer>());

			}

			int num_no_pillados = id_pasajeros.size();

			ArrayList<Boolean> vb = new ArrayList<Boolean> (id_pasajeros.size());

			for (int i = 0; i < id_pasajeros.size(); ++i) vb.add(false);


			while (num_no_pillados > 0) {

				for (int i = 0; i < id_conductores.size() && num_no_pillados > 0; ++i) {

					int aux = trayectos.get(i).size();
					int id_1 = trayectos.get(i).get(aux-1);

					int psj_1 = pasajeros.get(i).size() == 0 ? 0 : pasajeros.get(i).get(0);
					int psj_2 = pasajeros.get(i).size() <= 1 ? 0 : pasajeros.get(i).get(1);

					int x_act = id_1 > 0 ? cjt_usuarios.get(id_1 - 1).getCoordOrigenX() : cjt_usuarios.get(-(id_1 + 1)).getCoordDestinoX();
					int y_act = id_1 > 0 ? cjt_usuarios.get(id_1 - 1).getCoordOrigenY() : cjt_usuarios.get(-(id_1 + 1)).getCoordDestinoY();

					int num_pasajeros = pasajeros.get(i).size();

					int min_dist = 1000000;
					int id_posible = 0;

					for (int j = 0; j < id_pasajeros.size() && num_pasajeros < 2; ++j) {

						int x_p = cjt_usuarios.get(id_pasajeros.get(j) - 1).getCoordOrigenX();
						int y_p = cjt_usuarios.get(id_pasajeros.get(j) - 1).getCoordOrigenY();

						int d = Math.abs(x_p - x_act) + Math.abs(y_p - y_act);

						if (!vb.get(j) && min_dist > d) {
							min_dist = d;
							id_posible = j;
						}

					}

					boolean recoger = num_pasajeros == 0 || (num_pasajeros == 1 && min_dist < Math.abs(x_act - cjt_usuarios.get(id_pasajeros.get(id_posible)-1).getCoordDestinoX()) + Math.abs(y_act - cjt_usuarios.get(id_pasajeros.get(id_posible)-1).getCoordDestinoY()));


					if (recoger) {

						--num_no_pillados;
						int num_recoger = id_posible;
						vb.set(num_recoger, true);

						pasajeros.get(i).add(id_pasajeros.get(num_recoger));

						int x_dest = cjt_usuarios.get(id_pasajeros.get(num_recoger)-1).getCoordOrigenX();
						int y_dest = cjt_usuarios.get(id_pasajeros.get(num_recoger)-1).getCoordOrigenY();

						distancias.set(i, distancias.get(i) + Math.abs(x_act - x_dest) + Math.abs(y_act - y_dest));

						trayectos.get(i).add(id_pasajeros.get(num_recoger));


					}

					else {

						int xd;
						
						if (pasajeros.get(i).size() == 1) xd = 0;
						else xd = Math.abs(x_act - cjt_usuarios.get(pasajeros.get(i).get(0) - 1).getCoordDestinoX()) + Math.abs(y_act - cjt_usuarios.get(pasajeros.get(i).get(0) - 1).getCoordDestinoY()) < Math.abs(x_act - cjt_usuarios.get(pasajeros.get(i).get(1) - 1).getCoordDestinoX()) + Math.abs(y_act - cjt_usuarios.get(pasajeros.get(i).get(1) - 1).getCoordDestinoY()) ? 0 : 1;

						int id_dejar = pasajeros.get(i).get(xd);

						int x_dest = cjt_usuarios.get(id_dejar-1).getCoordDestinoX();
						int y_dest = cjt_usuarios.get(id_dejar-1).getCoordDestinoY();

						pasajeros.get(i).remove(xd);

						distancias.set(i, distancias.get(i) + Math.abs(x_act - x_dest) + Math.abs(y_act - y_dest));

						trayectos.get(i).add(-id_dejar);

						

					}


				}

			}

			
			
			for (int i = 0; i < distancias.size(); ++i) {

				int psj_1 = pasajeros.get(i).size() == 0 ? 0 : pasajeros.get(i).get(0);
				int psj_2 = pasajeros.get(i).size() <= 1 ? 0 : pasajeros.get(i).get(1);


				if (psj_1 != 0 && psj_2 != 0) {

					Random r = new Random();
					int primero = r.nextInt(2) == 0 ? psj_1 : psj_2;
					int segundo = primero == psj_1 ? psj_2 : psj_1;
				    
					int id_u = trayectos.get(i).get(trayectos.get(i).size() - 1);
				    int x_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoX() : cjt_usuarios.get(id_u-1).getCoordOrigenX();
				    int y_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoY() : cjt_usuarios.get(id_u-1).getCoordOrigenY();

					trayectos.get(i).add(-primero);
					trayectos.get(i).add(-segundo);

					Usuario u_prim = cjt_usuarios.get(primero-1);
					Usuario u_segu = cjt_usuarios.get(segundo-1);

					distancias.set(i, distancias.get(i) + Math.abs(x_a - u_prim.getCoordDestinoX()) + Math.abs(y_a - u_prim.getCoordDestinoY()));
					distancias.set(i, distancias.get(i) + Math.abs(u_prim.getCoordDestinoX() - u_segu.getCoordDestinoX()) + Math.abs(u_prim.getCoordDestinoY() - u_segu.getCoordDestinoY()));
				    
					Usuario conductor = cjt_usuarios.get(id_conductores.get(i)-1);
				    int x_f = conductor.getCoordDestinoX();
				    int y_f = conductor.getCoordDestinoY();
				    distancias.set(i, distancias.get(i) + Math.abs(u_segu.getCoordDestinoX() - x_f) + Math.abs(u_segu.getCoordDestinoY() - y_f));
				    trayectos.get(i).add(-id_conductores.get(i));

				}

				else if (psj_1 != 0) {

					int id_u = trayectos.get(i).get(trayectos.get(i).size() - 1);
				    int x_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoX() : cjt_usuarios.get(id_u-1).getCoordOrigenX();
				    int y_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoY() : cjt_usuarios.get(id_u-1).getCoordOrigenY();

					trayectos.get(i).add(-psj_1);

					Usuario conductor = cjt_usuarios.get(id_conductores.get(i)-1);
				    int x_f = conductor.getCoordDestinoX();
				    int y_f = conductor.getCoordDestinoY();

					if (id_u > 0) {

						distancias.set(i, distancias.get(i) + Math.abs(x_a - cjt_usuarios.get(id_u-1).getCoordDestinoX()) + Math.abs(y_a - cjt_usuarios.get(id_u-1).getCoordDestinoY()));

						x_a = cjt_usuarios.get(id_u-1).getCoordDestinoX();
						y_a = cjt_usuarios.get(id_u-1).getCoordDestinoY();
					}


				    distancias.set(i, distancias.get(i) + Math.abs(x_a - x_f) + Math.abs(y_a - y_f));
				    trayectos.get(i).add(-id_conductores.get(i));

				}

				else if (psj_2 != 0) {


					int id_u = trayectos.get(i).get(trayectos.get(i).size() - 1);
				    int x_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoX() : cjt_usuarios.get(id_u-1).getCoordOrigenX();
				    int y_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoY() : cjt_usuarios.get(id_u-1).getCoordOrigenY();

					trayectos.get(i).add(-psj_2);

					Usuario conductor = cjt_usuarios.get(id_conductores.get(i)-1);
				    int x_f = conductor.getCoordDestinoX();
				    int y_f = conductor.getCoordDestinoY();

					if (id_u > 0) {

						distancias.set(i, distancias.get(i) + Math.abs(x_a - cjt_usuarios.get(id_u-1).getCoordDestinoX()) + Math.abs(y_a - cjt_usuarios.get(id_u-1).getCoordDestinoY()));

						x_a = cjt_usuarios.get(id_u-1).getCoordDestinoX();
						y_a = cjt_usuarios.get(id_u-1).getCoordDestinoY();
					}
				    distancias.set(i, distancias.get(i) + Math.abs(x_a - x_f) + Math.abs(y_a - y_f));
				    trayectos.get(i).add(-id_conductores.get(i));

				}

				else {


					int id_u = trayectos.get(i).get(trayectos.get(i).size() - 1);
					Usuario conductor = cjt_usuarios.get(id_conductores.get(i)-1);
				    int x_f = conductor.getCoordDestinoX();
				    int y_f = conductor.getCoordDestinoY();
				    int x_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoX() : cjt_usuarios.get(id_u-1).getCoordOrigenX();
				    int y_a = id_u < 0 ? cjt_usuarios.get(-(id_u + 1)).getCoordDestinoY() : cjt_usuarios.get(id_u-1).getCoordOrigenY();
				    distancias.set(i, distancias.get(i) + Math.abs(x_a - x_f) + Math.abs(y_a - y_f));
				    trayectos.get(i).add(-id_conductores.get(i));
				}
				distancias.set(i, calculate_distance(i));
			}	

	}

	/**
 	* 
 	* @param car car to permutate
 	* @param id1 First id to swap
 	* @param id2 Second id to swap
 	* @return new distance
	* Fails if the resulting route leaves users before picking them up
	* id1 < id2
 	*/
	public boolean route_permutation(int car, int id1, int id2) {
		ArrayList<Integer> route = trayectos.get(car);
		ArrayList<Integer> copyroute = new ArrayList<Integer>(route);

		if (route.get(id1) > 0 || route.get(id2) < 0) {
			for (int i = id1; i <= id2; ++i) {
				if (route.get(i) == -route.get(id1) || route.get(i) == -route.get(id2)) return false;
			}
		}
		
		//System.out.println("Coche: " + car);
		//System.out.println("Posicion 1: " + id1 + "; Movimiento en cusetión: " +  route.get(id1));
		//System.out.println("Posicion 2: " + id2 + "; Movimiento en cusetión: " +  route.get(id2));

		if (route.get(id1) < 0 && route.get(id2) > 0) {
			int capacity = 2;
			for (int i = 1; i < id1; ++i) {
				if (route.get(i) > 0) --capacity;
				else ++capacity;
				if (capacity <= 0) {
					//System.out.println("Vamos mal!");
					return false;
				}
			}
		}

		
		//int anterior1 = route.get(id1 - 1);
		int user1 = route.get(id1);
		//int post1 = route.get(id1 + 1);

		//int anterior2 = route.get(id2 - 1);
		int user2 = route.get(id2);
		//int post2 = route.get(id2 + 1);

		/* 
		int dAnt = distance(anterior1, user1) + distance(user2, post2);
		int dNew = distance(anterior1, user2) + distance(user1, post2);

		
		if (user1 != anterior2) {
			dAnt += distance(anterior2, user2) + distance(user1, post1);
			dNew += distance(anterior2, user1) + distance(user2, post1);
		}
		*/
		
		int distance = distancias.get(car);
		
		route.set(id1, user2);
		route.set(id2, user1);
		
		int newDistance = calculate_distance(car);

		if (newDistance > 300) {
			trayectos.set(car, copyroute);
			return false;
		}

		distancias.set(car, newDistance);
		return true;
	}

	/**
	 * 
	 * @param car1 first car 
	 * @param car2 second car
	 * @param id1 id of the position on the route to swap on the first car (route1[id1] has to be positive)
	 * @param id2 id of the position on the route to swap on the second car (route2[id2] has to be positive)
	 * @return returns new distance
	 */
	public boolean passenjer_swap(int car1, int car2, int id1, int id2) {
		ArrayList<Integer> route1 = trayectos.get(car1);
		ArrayList<Integer> route2 = trayectos.get(car2);

		ArrayList<Integer> copy1 = new ArrayList<Integer> (route1);
		ArrayList<Integer> copy2 = new ArrayList<Integer>(route2);
		int Distance1 = distancias.get(car1);
		int Distance2 = distancias.get(car2);

		if (id1 == 0 || id1 == route1.size() - 1 || id2 == 0 || id2 == route2.size() - 1) return false;
		if (route1.get(id1) <= 0 || route2.get(id2) <= 0) return false;

		//for (Integer x : route1) System.out.print(x + " , ");
		//System.out.println("");

		//for (Integer x : route2) System.out.print(x + " , ");
		//System.out.println("");
		//System.out.println("Usuario a mover: " + route1.get(id1));
		//System.out.println("____________________________________");

		//System.out.println("Coche 1: " + car1);
		//System.out.println("Coche 2: " + car2);
		//System.out.println("Posicion 1: " + id1 + "; Movimiento en cuestión: " +  route1.get(id1));
		//System.out.println("Posicion 2: " + id2 + "; Movimiento en cuestión: " +  route2.get(id2));
		
		
		int job1, job2;
		job1 = job2 = -1;
		for (int i = id1 + 1; (i < route1.size()) && (job1 == -1); ++i) {
			if (route1.get(i) == -route1.get(id1)) {
				job1 = i;
			}
		}

		for (int i = id2 + 1; (i < route2.size()) && (job2 == -1); ++i) {
			if (route2.get(i) == -route2.get(id2)) {
				job2 = i;
			}
		}

		//System.out.println("Trabajo 1: " + job1 +  " Verificación: " + route1.get(job1));
		//System.out.println("Trabajo 1: " + job2 +  " Verificación: " + route2.get(job2));
 
		//if (job2 < 1 || job1 < 2 || job1 == route1.size() - 1 || job2 == route2.size() - 1) return;


		int user1Og =  route1.get(id1);
		int user1Dest =  route1.get(job1);
		int user2Og =  route2.get(id2);
		int user2Dest =  route2.get(job2);

		int distance1 = distancias.get(car1);
		int distance2 = distancias.get(car2);

		route1.set(id1, user2Og);
		route1.set(job1, user2Dest);

		route2.set(id2, user1Og);
		route2.set(job2, user1Dest);

		int dist1 = calculate_distance(car1);
		int dist2 = calculate_distance(car2);
		if ( dist1 > 300 || dist2 > 300) {
			trayectos.set(car1, copy1);
			distancias.set(car1, Distance1);

			trayectos.set(car2, copy2);
			distancias.set(car2, Distance2);
			return false;
		}

		distancias.set(car1, dist1);
		distancias.set(car2, dist2);

		return true;
		//System.out.println("Nueva ruta 1: ");
		//for (Integer x : trayectos.get(car1)) System.out.print(x + " , ");

		//System.out.println(" ");
		//System.out.println("Distancia 1 antigua: " + distance1);
		//System.out.println("Nueva distancia 1: " + distancias.get(car1));
		
		//System.out.println("Nueva ruta 2: ");
		//for (Integer x : trayectos.get(car2)) System.out.print(x + " , ");
		
		//System.out.println(" ");
		//System.out.println("Distancia 2 antigua: " + distance2);
		//System.out.println("Nueva distancia 2: " + distancias.get(car2));
	} 

	/**
	 * 
	 * @param carOg Origin car
	 * @param id id of the user to change 
	 * @param carDest car to go
	 * @return returns true if the operator has been succesfully implemented
	 */
	public boolean changeCar(int carOg, int id, int carDest) {
		ArrayList<Integer> routeOg = trayectos.get(carOg);
		ArrayList<Integer> routeDest = trayectos.get(carDest);
		ArrayList<Integer> copyOg = new ArrayList<Integer>(routeOg);
		ArrayList<Integer> copyDest = new ArrayList<Integer>(routeDest);
		
		

		int OgDistance = distancias.get(carOg);
		int DestDistance = distancias.get(carDest);

		if (routeOg.get(id) <= 0) return false;
		if (carOg == carDest) return false;

		int job = -1;
		for (int i = id + 1; (i < routeOg.size()) && (job == -1); ++i) {
			if (routeOg.get(i) == -routeOg.get(id)) {
				job = i;
			}
		}

		//for (Integer x : routeOg) System.out.print(x + " , ");
		//System.out.println("");

		//for (Integer x : routeDest) System.out.print(x + " , ");
		//System.out.println("");
		//System.out.println("Usuario a mover: " + routeOg.get(id) + ", trabajo en la posición: " + job);
		//System.out.println("____________________________________");

		int lastAction = routeDest.get(routeDest.size() - 1);
		routeDest.set(routeDest.size() - 1, routeOg.get(id));
		routeDest.add(routeOg.get(job));
		routeDest.add(lastAction);

		int distDest = calculate_distance(carDest);
		if (distDest > 300) {
			trayectos.set(carDest, copyDest);
			distancias.set(carDest, DestDistance);
			return false;
		}

		distancias.set(carDest, distDest);

		int conductor = routeOg.get(0);
		int usuario = routeOg.get(id);

		routeOg.remove(id);
		routeOg.remove(job - 1);
		distancias.set(carOg, calculate_distance(carOg));

		if (routeOg.size() == 2) {
			if (!reasignarConductor(carOg)) {
				trayectos.set(carDest, copyDest);
				distancias.set(carDest, DestDistance);
				trayectos.set(carOg, copyOg);
				distancias.set(carOg, OgDistance);
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 
	 * @param carOg Origin car
	 * @param id id of the user to change 
	 * @param carDest car to go
	 * @return returns true if the operator has been succesfully implemented
	 */
	public boolean changeCar2(int carOg, int id, int carDest) {
		ArrayList<Integer> routeOg = trayectos.get(carOg);
		ArrayList<Integer> routeDest = trayectos.get(carDest);
		
		if (routeOg.get(id) <= 0) return false;
		if (carOg == carDest) return false;

		int job = -1;
		for (int i = id + 1; (i < routeOg.size()) && (job == -1); ++i) {
			if (routeOg.get(i) == -routeOg.get(id)) {
				job = i;
			}
		}
		
		routeDest.add(1, routeOg.get(id));
		routeDest.add(2, routeOg.get(job));

		int distDest = calculate_distance(carDest);
		if (distDest > 300) {
			routeDest.remove(1);
			routeDest.remove(1);
			return false;
		}

		int distancia_original_dest = distancias.get(carDest);
		distancias.set(carDest, distDest);

		int conductor = routeOg.get(0);
		int usuario = routeOg.get(id);

		routeOg.remove(id);
		routeOg.remove(job - 1);

		int distancia_original_Og = distancias.get(carOg);
		distancias.set(carOg, calculate_distance(carOg));

		if (routeOg.size() == 2) {
			if (!reasignarConductor(carOg)) {
				routeOg.add(conductor);
				routeOg.add(usuario);
				routeOg.add(-usuario);
				routeOg.add(-conductor);

				routeDest.remove(1);
				routeDest.remove(1);
				distancias.set(carDest, distancia_original_dest);
				distancias.set(carOg, distancia_original_Og);
				return false;
			}
		}
		
		return true;
	}

	private boolean reasignarConductor(int car) {
		
		for (int i = 0; i < num_cond; ++ i) {
			if (changeCar(car, 0, i)) {
				trayectos.remove(car);
				distancias.remove(car);
				--num_cond;
				return true;
			}
		}

		return false;
	}

	private int distance(int id1, int id2) {
		int pos1X, pos1Y, pos2X, pos2Y;

		if (id1 < 0) {
			pos1X = cjt_usuarios.get(java.lang.Math.abs(id1) - 1).getCoordDestinoX();
			pos1Y = cjt_usuarios.get(java.lang.Math.abs(id1) - 1).getCoordDestinoY();
		}

		else {
			pos1X = cjt_usuarios.get(id1 - 1).getCoordOrigenX();
			pos1Y = cjt_usuarios.get(id1 - 1).getCoordOrigenY();
		}

		if (id2 < 0) {
			pos2X = cjt_usuarios.get(java.lang.Math.abs(id2) - 1).getCoordDestinoX();
			pos2Y = cjt_usuarios.get(java.lang.Math.abs(id2) - 1).getCoordDestinoY();
		}

		else {
			pos2X = cjt_usuarios.get(id2 - 1).getCoordOrigenX();
			pos2Y = cjt_usuarios.get(id2 - 1).getCoordOrigenY();
		}

		return java.lang.Math.abs(pos1X - pos2X) + java.lang.Math.abs(pos1Y - pos2Y);
	}

	/**
	 * @param route_id id of the route to check
	 * @return lenght of the route_id'th route
	*/
	public int getRouteLength(int route_id) {
		return trayectos.get(route_id).size();
	}

	/**
	 * @return number of cars used
	*/
	public int getNumCars() {
		return num_cond;
	}

	/**
	 * @return ArrayList con todas las distancias recorridas por cada coche
	 */
	public ArrayList<Integer> get_Distances() {
		return distancias;
	}

	private int calculate_distance(int car) {
		int distance = 0;
		ArrayList<Integer> actRoute = trayectos.get(car);
		for (int i = 0; i < actRoute.size() - 1; ++i) {
			distance += distance(actRoute.get(i), actRoute.get(i + 1));
		}

		return distance;
	}

	/**
	 * @return Comprueba si las distancias calculadas i las reales coinciden. En caso contrario escribe por pantalla el error
	 */
	public boolean checkDistances() {
		for (int i = 0; i < num_cond; ++i) {
			int actDist = calculate_distance(i);
			if (actDist != distancias.get(i)) {
				System.out.println("Coche número " + i + " no coincide :(");
				System.out.println("Distancia real = " + actDist);
				return false;
			}
			else if (actDist > 300) System.out.println(":[");
		}
		return true;
	}

	/**
	 * mamasita super andalusian fast super solver
	 * @return
	 */
	public ArrayList<Integer> distancias_coches(){
		return distancias;
	}
}

