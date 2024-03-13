declare module 'greet-by-time' {
    interface Greet {
        (name: string, hour: number): string;
        morningGreeting: string[];
        dayGreeting: string[];
        eveningGreeting: string[];
    }

    const greet: Greet;
    export default greet;
}