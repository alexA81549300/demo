use git2::Repository;
fn main() {
    println!("libgit2 OK. Initialiserer repo…");
    Repository::init("tmp-repo").unwrap();
    println!("Ferdig!");
}
