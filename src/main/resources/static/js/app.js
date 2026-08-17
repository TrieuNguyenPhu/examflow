document.addEventListener("DOMContentLoaded", () => {
    const menuToggle = document.querySelector("[data-menu-toggle]");
    const menu = document.querySelector("#primary-nav");
    menuToggle?.addEventListener("click", () => {
        const open = menu.classList.toggle("is-open");
        menuToggle.setAttribute("aria-expanded", String(open));
    });

    document.querySelectorAll("[data-dismiss-alert]").forEach((button) => {
        button.addEventListener("click", () => button.closest(".alert")?.remove());
    });

    document.querySelectorAll("form[data-confirm]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            if (!window.confirm(form.dataset.confirm)) event.preventDefault();
        });
    });

    const examApp = document.querySelector("[data-exam-app]");
    if (!examApp) return;

    const form = examApp.querySelector("#exam-form");
    const cards = [...examApp.querySelectorAll("[data-question]")];
    const paletteButtons = [...examApp.querySelectorAll("[data-question-jump]")];
    const previous = examApp.querySelector("[data-question-previous]");
    const next = examApp.querySelector("[data-question-next]");
    const timer = examApp.querySelector("[data-exam-timer]");
    const answerProgress = examApp.querySelector("[data-answer-progress]");
    const answeredCountLabel = examApp.querySelector("[data-answered-count]");
    const progressPercentLabel = examApp.querySelector("[data-progress-percent]");
    const examId = form?.querySelector('input[name="examId"]')?.value;
    const deadline = Number(timer?.dataset.deadline || 0);
    const draftKey = `examflow:draft:${examId}`;
    let current = 0;
    let submitting = false;
    let hasAnswers = false;

    const draftStorage = {
        get() {
            try { return sessionStorage.getItem(draftKey); }
            catch { return null; }
        },
        set(value) {
            try { sessionStorage.setItem(draftKey, value); }
            catch { /* Draft persistence is optional. */ }
        },
        remove() {
            try { sessionStorage.removeItem(draftKey); }
            catch { /* Draft persistence is optional. */ }
        }
    };

    try {
        const draft = JSON.parse(draftStorage.get());
        if (draft?.deadline === deadline) {
            Object.entries(draft.answers || {}).forEach(([name, value]) => {
                const radio = [...form.elements].find((element) => element.name === name && element.value === value);
                if (radio) radio.checked = true;
            });
        } else {
            draftStorage.remove();
        }
    } catch {
        draftStorage.remove();
    }

    const showQuestion = (index) => {
        if (index < 0 || index >= cards.length) return;
        cards.forEach((card, cardIndex) => {
            card.hidden = cardIndex !== index;
        });
        paletteButtons.forEach((button, buttonIndex) => {
            button.classList.toggle("is-current", buttonIndex === index);
            if (buttonIndex === index) button.setAttribute("aria-current", "step");
            else button.removeAttribute("aria-current");
        });
        current = index;
        previous.disabled = current === 0;
        next.disabled = current === cards.length - 1;
        cards[current]?.querySelector("input")?.focus({preventScroll: true});
    };

    previous?.addEventListener("click", () => showQuestion(current - 1));
    next?.addEventListener("click", () => showQuestion(current + 1));
    paletteButtons.forEach((button, index) => button.addEventListener("click", () => showQuestion(index)));
    const answeredCount = () => cards.filter((card) => card.querySelector('input[type="radio"]:checked')).length;
    const updateAnswerProgress = () => {
        const answered = answeredCount();
        const percent = cards.length === 0 ? 0 : Math.round(answered * 100 / cards.length);
        if (answerProgress) answerProgress.value = answered;
        if (answeredCountLabel) answeredCountLabel.textContent = `${answered} of ${cards.length} answered`;
        if (progressPercentLabel) progressPercentLabel.textContent = `${percent}%`;
    };
    examApp.querySelectorAll('input[type="radio"]').forEach((radio) => {
        const card = radio.closest("[data-question]");
        const index = cards.indexOf(card);
        if (radio.checked) {
            paletteButtons[index]?.classList.add("is-answered");
            hasAnswers = true;
        }
        radio.addEventListener("change", () => {
            paletteButtons[index]?.classList.add("is-answered");
            hasAnswers = true;
            updateAnswerProgress();
            const answers = {};
            examApp.querySelectorAll('input[type="radio"]:checked').forEach((answer) => {
                answers[answer.name] = answer.value;
            });
            draftStorage.set(JSON.stringify({deadline, answers}));
        });
    });
    updateAnswerProgress();

    form?.addEventListener("submit", (event) => {
        const unanswered = cards.length - answeredCount();
        const message = unanswered > 0
            ? `${unanswered} ${unanswered === 1 ? "question is" : "questions are"} unanswered. Submit anyway? You cannot change your answers afterward.`
            : "Submit this exam? You cannot change your answers afterward.";
        if (!submitting && !window.confirm(message)) {
            event.preventDefault();
            return;
        }
        submitting = true;
        draftStorage.remove();
    });

    window.addEventListener("beforeunload", (event) => {
        if (hasAnswers && !submitting) {
            event.preventDefault();
            event.returnValue = "";
        }
    });

    const updateTimer = () => {
        const remaining = Math.max(0, Math.ceil((deadline - Date.now()) / 1000));
        const minutes = Math.floor(remaining / 60).toString().padStart(2, "0");
        const seconds = (remaining % 60).toString().padStart(2, "0");
        timer.textContent = `${minutes}:${seconds}`;
        timer.classList.toggle("is-urgent", remaining <= 300);
        if (remaining === 0) {
            submitting = true;
            form?.requestSubmit();
            return;
        }
        window.setTimeout(updateTimer, 250);
    };

    showQuestion(0);
    updateTimer();
});
